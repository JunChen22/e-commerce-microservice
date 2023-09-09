package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.controller.PMS.ReviewAggregate;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Scheduler;

@RestController
@Api(tags = "", description = "")
@RequestMapping("/coupon")
public class CouponAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(CouponAggregate.class);

    private final WebClient webClient;
    private final Scheduler publishEventScheduler;

    @Value("${app.OMS-service.host}")
    String salesServiceURL;
    @Value("${app.OMS-service.port}")
    int port;

    @Autowired
    public CouponAggregate(WebClient.Builder  webClient, @Qualifier("publishEventScheduler")Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.publishEventScheduler = publishEventScheduler;
    }
}
