package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.dto.sms.UsedCouponHistory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import static java.util.logging.Level.FINE;

@RestController
@RequestMapping("/coupon")
@Api(tags = "Coupon related", description = "CRUD coupon by admin with right roles/permission")
public class CouponHistoryController {

    private static final Logger LOG = LoggerFactory.getLogger(CouponHistoryController.class);

    private final WebClient webClient;

    private final String SMS_SERVICE_URL = "http://sms:8080/coupon/history";

    @Autowired
    public CouponHistoryController(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @GetMapping("/getAllUsedCoupon")
    @ApiOperation(value = "return all the coupon used between two time")
    public Flux<UsedCouponHistory> couponUsed() {
        // TODO: add default value for the two times, currently all used coupons
        String url = SMS_SERVICE_URL+ "/getAllUsedCoupon";

        return webClient.get().uri(url).retrieve().bodyToFlux(UsedCouponHistory.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    };

    @GetMapping("/getUserCoupon/{userId}")
    @ApiOperation(value = "shows how many coupon(amount saved) a user used")
    public Flux<UsedCouponHistory> getUserCoupon(@PathVariable int userId) {
        String url = SMS_SERVICE_URL + "/getUserCoupon/" + userId;

        return webClient.get().uri(url).retrieve().bodyToFlux(UsedCouponHistory.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }
}
