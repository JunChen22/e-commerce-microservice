package com.itsthatjun.ecommerce.service.SMS;

import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Mono;

public interface CouponService {

    @ApiOperation("Check coupon and return discount amount")
    Mono<Double> checkCoupon(String couponCode, int userId);
}
