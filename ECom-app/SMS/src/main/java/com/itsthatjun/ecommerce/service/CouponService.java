package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.CouponDiscount;
import com.itsthatjun.ecommerce.exceptions.CouponException;
import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.mbg.model.CouponExample;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface  CouponService {

    // TODO: after security implement it, return discount amount based on shopping cart.
    @ApiOperation("Return discount amount based on item list")
    Mono<Double> checkDiscount(String couponCode, int userId);

    @ApiOperation("Get All coupons that can apply to a product")
    Flux<Coupon> getCouponForProduct(int productId);

    @ApiOperation("update used coupon count and history")
    Mono<Void> updateUsedCoupon(String code, int orderId, int memberId);

    @ApiOperation(value = "")
    Mono<Coupon> createCoupon(Coupon newCoupon, Map<String, Integer> skuQuantity);

    @ApiOperation(value = "Get all coupon")
    Flux<Coupon> getAllCoupon();

    @ApiOperation(value = "Get detail of a coupon by id")
    Mono<Coupon> getACoupon(int id);

    @ApiOperation(value = "Update coupon info's like discount type, amount, date, count, status, and name")
    Mono<Coupon> updateCoupon(Coupon updateCoupon, Map<String, Integer> skuMap);

    @ApiOperation(value = "")
    Mono<Void> deleteCoupon(int couponId);
}
