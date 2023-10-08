package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.event.oms.OmsCompletionEvent;
import com.itsthatjun.ecommerce.dto.event.oms.OmsOrderEvent;
import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.config.URLUtils;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import com.itsthatjun.ecommerce.security.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.servlet.http.HttpServletRequest;

import static com.itsthatjun.ecommerce.dto.event.oms.OmsCompletionEvent.Type.PAYMENT_FAILURE;
import static com.itsthatjun.ecommerce.dto.event.oms.OmsCompletionEvent.Type.PAYMENT_SUCCESS;
import static com.itsthatjun.ecommerce.dto.event.oms.OmsOrderEvent.Type.CANCEL_ORDER;
import static com.itsthatjun.ecommerce.dto.event.oms.OmsOrderEvent.Type.GENERATE_ORDER;
import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@RestController
@Api(tags = "Order controller", description = "Order controller")
@RequestMapping("/order")
public class OrderAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(OrderAggregate.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    public static final String PAYPAL_SUCCESS_URL = "order/payment/success";

    public static final String PAYPAL_CANCEL_URL = "order/payment/cancel";

    private final String OMS_SERVICE_URL = "http://oms/order";

    @Autowired
    public OrderAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder  webClient, StreamBridge streamBridge,
                          @Qualifier("publishEventScheduler")Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @ApiOperation("Get Order Detail by serial number")
    @GetMapping("/detail/{orderSn}")
    public Mono<Orders> detail(@PathVariable String orderSn) {
        String url = OMS_SERVICE_URL + "/detail/" + orderSn;
        LOG.debug("Will call the detail API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @ApiOperation("Get member order based on status code and page size")
    @ApiImplicitParam(name = "status", value = "Order status：-1->All；0->wait for payment；1-> fulfilling；2->send；3->complete；4->closed",
            defaultValue = "-1", allowableValues = "-1,0,1,2,3,4", paramType = "query", dataType = "int")
    @GetMapping("/list")
    public Flux<Orders> list(@RequestParam(required = false, defaultValue = "-1")  int status,
                             @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                             @RequestParam(required = false, defaultValue = "5") Integer pageSize) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

        String url = OMS_SERVICE_URL + "/list";
        LOG.debug("Will call the list API on URL: {}", url);

        return webClient.get().uri(url).header("X-UserId", String.valueOf(userId)).retrieve().bodyToFlux(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @PostMapping("/generateOrder")
    @ApiOperation(value = "Generate order based on shopping cart, actual transaction")
    public Mono<OrderParam> generateOrder(@RequestBody OrderParam orderParam, HttpServletRequest request){
        String successUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_SUCCESS_URL;
        String cancelUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_CANCEL_URL;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

        return Mono.fromCallable(() -> {
            sendOrderMessage("order-out-0", new OmsOrderEvent(GENERATE_ORDER, userId, null, orderParam, successUrl, cancelUrl));
            return orderParam;
        }).subscribeOn(publishEventScheduler);
    }

    @GetMapping("/payment/success")
    @ApiOperation("after success paypal payment, actual processing the order , unlock stocks and update info's like coupon and stocks")
    public Mono<String> successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId){
        return Mono.fromCallable(() -> {
            sendOrderCompleteMessage("orderComplete-out-0", new OmsCompletionEvent(PAYMENT_SUCCESS, "", paymentId, payerId));
            return "payment success";
        }).subscribeOn(publishEventScheduler);
    }

    @GetMapping("/payment/cancel/{orderSn}")
    @ApiOperation("Payment failure feedback")
    public Mono<String> payFail(@PathVariable String orderSn, @RequestParam("token") String token) {
        // TODO: make pay later feature with message queue TTL, store the token for later payment.
        //     example https://www.sandbox.paypal.com/cgi-bin/webscr?cmd=_express-checkout&token=EC-2LX47521TW024812X
        //     and need to set in Payment object for how long the transaction will stay up.
        return Mono.fromCallable(() -> {
            sendOrderCompleteMessage("orderComplete-out-0", new OmsCompletionEvent(PAYMENT_FAILURE, orderSn, token, ""));
            return "payment fail";
        }).subscribeOn(publishEventScheduler);
    }

    @PostMapping("/cancelOrder/{orderSn}")
    @ApiOperation("Cancel order if before sending the order out.")
    public Mono<Void> cancelUserOrder(@PathVariable String orderSn) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

        return Mono.fromRunnable(() -> {
            sendOrderMessage("orderComplete-out-0", new OmsOrderEvent(CANCEL_ORDER, userId, orderSn, null, null, null));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendOrderMessage(String bindingName, OmsOrderEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getUserId())
                .build();
        streamBridge.send(bindingName, message);
    }

    private void sendOrderCompleteMessage(String bindingName, OmsCompletionEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
