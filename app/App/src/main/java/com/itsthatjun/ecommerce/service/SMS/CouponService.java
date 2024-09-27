package com.itsthatjun.ecommerce.service.SMS;

import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CouponService {

    @ApiOperation("Check coupon and return discount amount")
    Mono<BigDecimal> checkCoupon(String couponCode, int userId);
}
