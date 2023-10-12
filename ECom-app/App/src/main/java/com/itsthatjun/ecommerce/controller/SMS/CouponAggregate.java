package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.security.UserContext;
import com.itsthatjun.ecommerce.service.SMS.impl.CouponServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Api(tags = "Coupon controller", description = "Coupon controller")
@RequestMapping("/coupon")
public class CouponAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(CouponAggregate.class);

    private final CouponServiceImpl couponService;

    @Autowired
    public CouponAggregate(CouponServiceImpl couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/check")
    @ApiOperation("Check coupon and return discount amount")
    public Mono<Double> checkCoupon(@RequestParam String couponCode) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();

        return couponService.checkCoupon(couponCode, userId);
    }
}
