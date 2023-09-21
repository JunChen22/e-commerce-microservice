package com.itsthatjun.ecommerce.controller.UMS;

import com.itsthatjun.ecommerce.controller.PMS.ReviewAggregate;
import com.itsthatjun.ecommerce.dto.event.PmsReviewEvent;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

import static java.util.logging.Level.FINE;

@RestController
@Api(tags = "", description = "")
@RequestMapping("/user")
public class UserAggregate {
    private static final Logger LOG = LoggerFactory.getLogger(UserAggregate.class);

    private final StreamBridge streamBridge;

    private final WebClient webClient;

    private final Scheduler publishEventScheduler;

    private final String UMS_SERVICE_URL = "http://ums";

    @Autowired
    public UserAggregate(StreamBridge streamBridge, WebClient webClient, Scheduler publishEventScheduler) {
        this.streamBridge = streamBridge;
        this.webClient = webClient;
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

    public Mono<Health> getUmsHealth() {
        return getHealth(UMS_SERVICE_URL);
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
