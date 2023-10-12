package com.itsthatjun.ecommerce.service.SMS.impl;

import com.itsthatjun.ecommerce.dto.sms.UsedCouponHistory;
import com.itsthatjun.ecommerce.mbg.model.CouponHistory;
import com.itsthatjun.ecommerce.service.SMS.CouponHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static java.util.logging.Level.FINE;

@Service
public class CouponHistoryServiceImpl implements CouponHistoryService {

    private static final Logger LOG = LoggerFactory.getLogger(CouponHistoryServiceImpl.class);

    private final WebClient webClient;

    private final String SMS_SERVICE_URL = "http://sms:8080/coupon/history";

    @Autowired
    public CouponHistoryServiceImpl(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @Override
    public Flux<UsedCouponHistory> couponUsed() {
        // TODO: add default value for the two times, currently all used coupons
        String url = SMS_SERVICE_URL+ "/getAllUsedCoupon";

        return webClient.get().uri(url).retrieve().bodyToFlux(UsedCouponHistory.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    };

    @Override
    public Flux<CouponHistory> getUserCoupon(int userId) {
        String url = SMS_SERVICE_URL + "/getUserCoupon/" + userId;

        return webClient.get().uri(url).retrieve().bodyToFlux(CouponHistory.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }
}
