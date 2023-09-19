package com.itsthatjun.ecommerce.controller.OMS;

import com.itsthatjun.ecommerce.dto.event.OmsCartEvent;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Scheduler;

@RestController
@Api(tags = "", description = "")
@RequestMapping("/return")
public class ReturnAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ReturnAggregate.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    @Value("${app.OMS-service.host}")
    String orderServiceURL;
    @Value("${app.OMS-service.port}")
    int port;

    public ReturnAggregate(WebClient.Builder  webClient, StreamBridge streamBridge,
                           @Qualifier("publishEventScheduler")Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    private void sendMessage(String bindingName, OmsCartEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getKey())
                .build();
        streamBridge.send(bindingName, message);
    }
}
