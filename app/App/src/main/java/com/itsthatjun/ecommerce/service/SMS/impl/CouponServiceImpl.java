package com.itsthatjun.ecommerce.service.SMS.impl;

import com.itsthatjun.ecommerce.service.SMS.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;

@Service
public class CouponServiceImpl implements CouponService {

    private static final Logger LOG = LoggerFactory.getLogger(CouponServiceImpl.class);

    private final WebClient webClient;

    private final String SMS_SERVICE_URL = "http://sms/coupon";

    @Autowired
    public CouponServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<Double> checkCoupon(String couponCode, int userId) {
        // TODO: currently return total amount, need to change to return individual discount.
        //  might return something like <String, Double> skuDiscount
        String url = SMS_SERVICE_URL + "/check?couponCode=" + couponCode;
        LOG.debug("Will call the checkCoupon API on URL: {}", url);

        return webClient.get().uri(url).header("X-UserId", String.valueOf(userId)).retrieve().bodyToMono(Double.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }
}
