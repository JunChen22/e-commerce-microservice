package com.itsthatjun.ecommerce.controller.SMS;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;

@RestController
@Api(tags = "Coupon controller", description = "Coupon controller")
@RequestMapping("/coupon")
public class CouponAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(CouponAggregate.class);

    private final WebClient webClient;

    private final String SMS_SERVICE_URL = "http://sms/coupon";

    @Autowired
    public CouponAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder  webClient) {
        this.webClient = webClient.build();
    }

    @GetMapping("/check")
    @ApiOperation("Check coupon and return discount amount")
    public Mono<Double> checkCoupon(@RequestParam String couponCode) {
        // TODO: currently reutrn total amount, need to change to return individual discount.
        //  might return something like <String, Double> skuDiscount

        // TODO: don't send in the whole cart, send in coupon and user id(security not implement so no jwt)
        //   and coupon service ask shoppin cart service for current users cart and send back discount amount
        String url = SMS_SERVICE_URL + "/check?code=" + couponCode;
        LOG.debug("Will call the checkCoupon API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(Double.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }
}
