package com.itsthatjun.ecommerce.service.OMS.impl;

import com.itsthatjun.ecommerce.dto.oms.OrderDetail;
import com.itsthatjun.ecommerce.dto.oms.OrderStatus;
import com.itsthatjun.ecommerce.dto.oms.ReturnStatusCode;
import com.itsthatjun.ecommerce.dto.oms.event.OmsAdminOrderEvent;
import com.itsthatjun.ecommerce.mbg.model.Orders;
import com.itsthatjun.ecommerce.service.OMS.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.oms.event.OmsAdminOrderEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String OMS_SERVICE_URL = "http://oms:8080/order";

    @Autowired
    public OrderServiceImpl(WebClient.Builder webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Flux<Orders> listAllOrder(OrderStatus statusCode) {
        String url = OMS_SERVICE_URL + "/admin/all?statusCode=" + statusCode.getCode();

        return webClient.get().uri(url).retrieve().bodyToFlux(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Flux<Orders> getUserOrders(int userId) {
        String url = OMS_SERVICE_URL + "/admin/user/" + userId;

        return webClient.get().uri(url).retrieve().bodyToFlux(Orders.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<OrderDetail> getOrder(String serialNumber) {
        String url = OMS_SERVICE_URL+ "/admin/detail/" + serialNumber;

        return webClient.get().uri(url).retrieve().bodyToMono(OrderDetail.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Mono<OrderDetail> createOrder(OrderDetail orderDetail, int userId, String reason, String operatorName) {
        return Mono.fromCallable(() -> {
            sendMessage("order-out-0", new OmsAdminOrderEvent(GENERATE_ORDER, userId, orderDetail, reason, operatorName));
            return orderDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<OrderDetail> updateOrder(OrderDetail orderDetail, String reason, String operatorName) {
        return Mono.fromCallable(() -> {
            sendMessage("order-out-0", new OmsAdminOrderEvent(UPDATE_ORDER, null, orderDetail, reason, operatorName));
            return orderDetail;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> cancelOrder(String serialNumber, int userId, String reason, String operatorName) {
        return Mono.fromRunnable(() -> {
            OrderDetail orderDetail = new OrderDetail();
            Orders orderTobeCancelled = new Orders();
            orderTobeCancelled.setOrderSn(serialNumber);
            orderDetail.setOrders(orderTobeCancelled);
            sendMessage("order-out-0", new OmsAdminOrderEvent(CANCEL_ORDER, userId, orderDetail, reason, operatorName));
        }).subscribeOn(publishEventScheduler).then();
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
