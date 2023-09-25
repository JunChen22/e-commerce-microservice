package com.itsthatjun.ecommerce.controller.UMS;

import com.itsthatjun.ecommerce.dto.event.pms.PmsReviewEvent;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static java.util.logging.Level.FINE;

@RestController
@Api(tags = "User controller", description = "user controller")
@RequestMapping("/user")
public class UserAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(UserAggregate.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String UMS_SERVICE_URL = "http://ums/user";

    @Autowired
    public UserAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClient, StreamBridge streamBridge,
                         @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    // TODO: after security implemented it
    //      register
    //      update info
    //      get notification, order status, return status, some admin notes and etc.
    //      delete account, just delete status changed for archive.

    private void sendMessage(String bindingName, PmsReviewEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("partitionKey", event.getUserId())
                .build();
        streamBridge.send(bindingName, message);
    }
}
