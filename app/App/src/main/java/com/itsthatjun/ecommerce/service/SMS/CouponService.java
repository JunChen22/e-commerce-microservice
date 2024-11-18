package com.itsthatjun.ecommerce.service.SMS;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface CouponService {

    /**
     * Check coupon and return discount amount TODO: might need to change this. send cart items to check.
     *                                              when trying coupon code when no user logged in, return total amount.
     *
     * @param couponCode coupon code
     * @return discount amount
     */
    Mono<BigDecimal> checkCoupon(String couponCode);
}
