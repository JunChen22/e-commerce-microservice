package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.controller.PMS.ReviewAggregate;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static java.util.logging.Level.FINE;

@RestController
@Api(tags = "", description = "")
@RequestMapping("/coupon")
public class CouponAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(CouponAggregate.class);

    private final WebClient webClient;

    private final Scheduler publishEventScheduler;

    private final String SMS_SERVICE_URL = "http://sms";

    @Autowired
    public CouponAggregate(WebClient.Builder  webClient, @Qualifier("publishEventScheduler")Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.publishEventScheduler = publishEventScheduler;
    }

    public Mono<Health> getSmsHealth() {
        return getHealth(SMS_SERVICE_URL);
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
