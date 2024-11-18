package com.itsthatjun.ecommerce.service.SMS.impl;

import com.itsthatjun.ecommerce.security.SecurityUtil;
import com.itsthatjun.ecommerce.service.SMS.CouponService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

import static java.util.logging.Level.FINE;

@Service
public class CouponServiceImpl implements CouponService {

    private static final Logger LOG = LoggerFactory.getLogger(CouponServiceImpl.class);

    private final WebClient webClient;

    private final SecurityUtil securityUtil;

    private final String SMS_SERVICE_URL = "http://sms/coupon";

    @Autowired
    public CouponServiceImpl(WebClient webClient, SecurityUtil securityUtil) {
        this.webClient = webClient;
        this.securityUtil = securityUtil;
    }

    @Override
    public Mono<BigDecimal> checkCoupon(String couponCode) {
        // TODO: currently return total amount, need to change to return individual discount.
        //  might return something like <String, Double> skuDiscount

        return securityUtil.getJwtToken()
                .zipWith(securityUtil.getMemberId()) // Combine the JWT and memberId
                .flatMap(tuple -> {
                    String jwt = tuple.getT1();
                    UUID memberId = tuple.getT2();
                    String url = SMS_SERVICE_URL + "/check?couponCode=" + couponCode;
                    LOG.debug("Will call the checkCoupon API on URL: {}", url);

                    return webClient.get().uri(url)
                            .header("X-MemberId", String.valueOf(memberId))
                            .retrieve().bodyToMono(BigDecimal.class)
                            .log(LOG.getName(), FINE)
                            .onErrorResume(WebClientResponseException.class, ex -> {
                                LOG.error("Error calling SMS service: {}", ex.getMessage());
                                return Mono.empty();
                            });
                });
    }
}
