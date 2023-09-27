package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.oms.OrderDetail;
import com.itsthatjun.ecommerce.dto.oms.OrderParam;
import com.itsthatjun.ecommerce.dto.oms.event.OmsAdminOrderEvent;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.oms.event.OmsAdminOrderEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@RequestMapping("/order")
@Api(tags = "Order related", description = "retrieve information about an order(s) and change order")
public class OrderController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderController.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String OMS_SERVICE_URL = "http://oms/order";

    @Autowired
    public OrderController(WebClient.Builder webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/payment")
    @ApiOperation(value = "List all orders that need to be paid")
    public Flux<Orders> listAllOrdersWaitForPayment(){
        String url = OMS_SERVICE_URL + "/order/admin/payment";

        return webClient.get().uri(url).retrieve().bodyToFlux(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/fulfill")
    @ApiOperation(value = "List all orders that need to be fulfill/open")
    public Flux<Orders> listAllFulfullingOrders(){
        String url = OMS_SERVICE_URL + "/order/admin/fulfill";

        return webClient.get().uri(url).retrieve().bodyToFlux(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/send")
    @ApiOperation(value = "List all orders that are send")
    public Flux<Orders> listAllSendOrders(){
        String url = OMS_SERVICE_URL + "/order/admin/send";

        return webClient.get().uri(url).retrieve().bodyToFlux(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/complete")
    @ApiOperation(value = "List all orders that are delivered")
    public Flux<Orders> listAl(){
        String url = OMS_SERVICE_URL + "/order/admin/complete";

        return webClient.get().uri(url).retrieve().bodyToFlux(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/user/{userId}")
    @ApiOperation(value = "get all orders made by user")
    public Flux<Orders> getUserOrders(@PathVariable int userId){
        String url = OMS_SERVICE_URL + "/order/admin/user/" + userId;

        return webClient.get().uri(url).retrieve().bodyToFlux(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/{serialNumber}")
    @ApiOperation(value = "look up a order by serial number")
    public Mono<Orders> getOrder(@PathVariable String serialNumber){
        String url = OMS_SERVICE_URL+ "/order/admin/" + serialNumber;

        return webClient.get().uri(url).retrieve().bodyToMono(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @PostMapping("/create")
    @ApiOperation(value = "create order")
    public void createOrder(@RequestBody OrderDetail orderDetail, @RequestParam int userId){
        Mono.fromRunnable(() -> sendMessage("order-out-0", new OmsAdminOrderEvent(GENERATE_ORDER, userId, orderDetail, null, "")))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/update")
    @ApiOperation(value = "update a order")
    public void updateOrder(@RequestBody OrderDetail orderDetail){
        Mono.fromRunnable(() -> sendMessage("order-out-0", new OmsAdminOrderEvent(UPDATE_ORDER, null, orderDetail, "", "")))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @DeleteMapping("/delete/{serialNumber}")
    @ApiOperation(value = "delete a order by serial number")
    public void deleteOrder(@PathVariable String serialNumber, @RequestParam int userId){
        Mono.fromRunnable(() -> sendMessage("order-out-0", new OmsAdminOrderEvent(CANCEL_ORDER, userId, null, null, "")))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    private void sendMessage(String bindingName, OmsAdminOrderEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
