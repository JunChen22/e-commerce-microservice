package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.event.PmsReviewEvent;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/review")
public class ReviewAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewAggregate.class);

    private final String bindingName = "review-out-0";
    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    @Value("${app.PMS-service.host}")
    String reviewServiceURL;
    @Value("${app.PMS-service.port}")
    int port;

    @Autowired
    public ReviewAggregate(WebClient.Builder  webClient, StreamBridge streamBridge,
                          @Qualifier("publishEventScheduler")Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    private void sendMessage(String bindingName, PmsReviewEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getUserId())
                .build();
        streamBridge.send(bindingName, message);
    }
}
