package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.event.OmsCompletionEvent;
import com.itsthatjun.ecommerce.dto.event.OmsOrderEvent;
import com.itsthatjun.ecommerce.dto.OrderParam;
import com.itsthatjun.ecommerce.config.URLUtils;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

import static com.itsthatjun.ecommerce.dto.event.OmsCompletionEvent.Type.PAYMENT_FAILURE;
import static com.itsthatjun.ecommerce.dto.event.OmsCompletionEvent.Type.PAYMENT_SUCCESS;
import static com.itsthatjun.ecommerce.dto.event.OmsOrderEvent.Type.GENERATE_ORDER;
import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@RestController
@Api(tags = "", description = "")
@RequestMapping("/order")
public class OrderAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(OrderAggregate.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    public static final String PAYPAL_SUCCESS_URL = "order/success";

    public static final String PAYPAL_CANCEL_URL = "order/cancel";

    private final String OMS_SERVICE_URL = "http://oms";

    @Autowired
    public OrderAggregate(WebClient.Builder  webClient, StreamBridge streamBridge,
                          @Qualifier("publishEventScheduler")Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @ApiOperation("Get Order Detail by serial number")
    @GetMapping("/detail/{orderSn}")
    public Mono<Orders> detail(@PathVariable String orderSn) {
        String url = OMS_SERVICE_URL + "/order/detail/{" + orderSn + "}";

        return webClient.get().uri(url).retrieve().bodyToMono(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @ApiOperation("Get member order based on status code and page size")
    @ApiImplicitParam(name = "status", value = "Order status：-1->All；0->wait for payment；1-> fulfilling；2->send；3->complete；4->closed",
            defaultValue = "-1", allowableValues = "-1,0,1,2,3,4", paramType = "query", dataType = "int")
    @GetMapping("/list")
    public Flux<Orders> list(@RequestParam(required = false, defaultValue = "-1")  int status,
                             @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                             @RequestParam(required = false, defaultValue = "5") Integer pageSize,
                             @RequestParam int userId) {
        String url = OMS_SERVICE_URL + "/cart/list?userId=" + userId;

        return webClient.get().uri(url).retrieve().bodyToFlux(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @PostMapping("/generateOrder")
    @ApiOperation(value = "Generate order based on shopping cart, actual transaction")
    public Map<String, Object> generateOrder(@RequestBody OrderParam orderParam , HttpServletRequest request, @RequestParam int userId){

        String successUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_SUCCESS_URL;
        String cancelUrl = URLUtils.getBaseURl(request) + "/" + PAYPAL_CANCEL_URL;
        sendOrderMessage("order-out-0", new OmsOrderEvent(GENERATE_ORDER, userId, orderParam, successUrl, cancelUrl));
        return null;
    }

    @GetMapping("/success")
    @ApiOperation("after success paypal payment, actual processing the order , unlock stocks and update info's like coupon and stocks")
    public String successPay(@RequestParam("paymentId") String paymentId, @RequestParam("PayerID") String payerId,
                             @RequestParam String orderSn){
        sendOrderCompleteMessage("orderComplete-out-0", new OmsCompletionEvent(PAYMENT_SUCCESS, orderSn, paymentId, payerId));
        return "payment success";
    }

    @GetMapping("/cancel")
    @ApiOperation("Payment failure feedback")
    public String payFail(@RequestParam String orderSn) {
        sendOrderCompleteMessage("orderComplete-out-0", new OmsCompletionEvent(PAYMENT_FAILURE, "", "", ""));
        return "payment fail";
    }

    @ApiOperation("Cancel order")
    @PostMapping("/cancelOrder/{orderSn}")
    public String cancelUserOrder(@PathVariable String orderSn) {
        return null;
    }

    // TODO: redis or like Quartz
    // timed/schedule depend on the deliver time and check UPS
    // then called to change status by redis.
    @ApiOperation("Member received deliver, update order status")
    @PostMapping(value = "/confirmReceiveOrder/{orderId}")
    public String confirmReceiveOrder(@PathVariable int orderId) {
        return null;
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
                .setHeader("partitionKey", event.getOrderSN())
                .build();
        streamBridge.send(bindingName, message);
    }

    public Mono<Health> getOmsHealth() {
        return getHealth(OMS_SERVICE_URL);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log(LOG.getName(), FINE);
    }
}
