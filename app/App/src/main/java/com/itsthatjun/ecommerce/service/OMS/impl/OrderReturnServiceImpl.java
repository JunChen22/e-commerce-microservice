package com.itsthatjun.ecommerce.service.OMS.impl;

import com.itsthatjun.ecommerce.dto.oms.ReturnParam;
import com.itsthatjun.ecommerce.dto.event.oms.OmsReturnEvent;
import com.itsthatjun.ecommerce.dto.oms.ReturnDetail;
import com.itsthatjun.ecommerce.service.OMS.OrderReturnService;
import com.itsthatjun.ecommerce.service.UMS.impl.UserServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.event.oms.OmsReturnEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class OrderReturnServiceImpl implements OrderReturnService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderReturnServiceImpl.class);

    private final UserServiceImpl userService;

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String OMS_SERVICE_URL = "http://oms/order/return";

    public OrderReturnServiceImpl(UserServiceImpl userService, WebClient webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler")Scheduler publishEventScheduler) {
        this.userService = userService;
        this.webClient = webClient;
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    public Mono<ReturnDetail> checkStatus(String orderSn) {
        int userId = userService.getUserId();
        String url = OMS_SERVICE_URL + "/status/" + orderSn;
        LOG.debug("Will call the checkStatus API on URL: {}", url);

        return webClient.get().uri(url).header("X-UserId", String.valueOf(userId)).retrieve().bodyToMono(ReturnDetail.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Mono<ReturnParam> applyForReturn(ReturnParam returnParam) {
        return Mono.fromCallable(() -> {
            int userId = userService.getUserId();
            sendMessage("return-out-0", new OmsReturnEvent(APPLY, userId, returnParam));
            return returnParam;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<ReturnParam> updateReturn(ReturnParam returnParam) {
        return Mono.fromCallable(() -> {
            int userId = userService.getUserId();
            sendMessage("return-out-0", new OmsReturnEvent(UPDATE, userId, returnParam));
            return returnParam;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> cancelReturn(String orderSn) {
        return Mono.fromRunnable(() -> {
            int userId = userService.getUserId();
            ReturnParam returnRequest = new ReturnParam();
            returnRequest.setOrderSn(orderSn);
            sendMessage("return-out-0", new OmsReturnEvent(CANCEL, userId, returnRequest));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, OmsReturnEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
