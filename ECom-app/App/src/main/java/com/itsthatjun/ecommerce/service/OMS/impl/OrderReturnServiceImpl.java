package com.itsthatjun.ecommerce.service.OMS.impl;

import com.itsthatjun.ecommerce.dto.ReturnParam;
import com.itsthatjun.ecommerce.dto.event.oms.OmsReturnEvent;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import com.itsthatjun.ecommerce.service.OMS.OrderReturnService;
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

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String OMS_SERVICE_URL = "http://oms/order/return";

    public OrderReturnServiceImpl(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder  webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler")Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    public Mono<ReturnRequest> checkStatus(String orderSn, int userId) {
        String url = OMS_SERVICE_URL + "/status/" + orderSn;
        LOG.debug("Will call the checkStatus API on URL: {}", url);

        return webClient.get().uri(url).header("X-UserId", String.valueOf(userId)).retrieve().bodyToMono(ReturnRequest.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Mono<ReturnParam> applyForReturn(ReturnParam returnParam, int userId) {
        return Mono.fromCallable(() -> {
            sendMessage("return-out-0", new OmsReturnEvent(APPLY, userId, returnParam.getReturnRequest(), returnParam));
            return returnParam;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<ReturnParam> updateReturn(ReturnParam returnParam, int userId) {
        return Mono.fromCallable(() -> {
            ReturnRequest returnRequest = returnParam.getReturnRequest();
            sendMessage("return-out-0", new OmsReturnEvent(UPDATE, userId, returnRequest, returnParam));
            return returnParam;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> cancelReturn(String orderSn, int userId) {
        return Mono.fromRunnable(() -> {
            ReturnRequest returnRequest = new ReturnRequest();
            returnRequest.setOrderSn(orderSn);
            sendMessage("return-out-0", new OmsReturnEvent(CANCEL, userId, returnRequest, null));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, OmsReturnEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}