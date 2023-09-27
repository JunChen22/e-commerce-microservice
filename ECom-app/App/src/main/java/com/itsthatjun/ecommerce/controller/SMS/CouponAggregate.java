package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.security.UserContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public CouponAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @GetMapping("/check")
    @ApiOperation("Check coupon and return discount amount")
    public Mono<Double> checkCoupon(@RequestParam String couponCode) {
        // TODO: currently return total amount, need to change to return individual discount.
        //  might return something like <String, Double> skuDiscount
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

        String url = SMS_SERVICE_URL + "/check?couponCode=" + couponCode;

        System.out.println("checking for: " + url);
        LOG.debug("Will call the checkCoupon API on URL: {}", url);

        return webClient.get().uri(url).header("X-UserId", String.valueOf(userId)).retrieve().bodyToMono(Double.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }
}
