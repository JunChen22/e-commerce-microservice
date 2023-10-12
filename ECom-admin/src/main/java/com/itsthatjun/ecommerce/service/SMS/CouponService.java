package com.itsthatjun.ecommerce.service.SMS;

import com.itsthatjun.ecommerce.dto.sms.CouponSale;
import com.itsthatjun.ecommerce.mbg.model.Coupon;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CouponService {

    @ApiOperation(value = "return all working non-expired coupon")
    Flux<Coupon> listAll();

    @ApiOperation(value = "Get coupons that works with a product")
    Mono<Coupon> list(int couponId);

    @ApiOperation(value = "Get coupons that works with a product")
    Flux<Coupon> getCouponForProduct(int productId);

    @ApiOperation(value = "create a coupon")
    Mono<CouponSale>  create(CouponSale couponSale, String operator);

    @ApiOperation(value = "update a coupon")
    Mono<CouponSale> update(CouponSale updatedCouponSale, String operator);

    @ApiOperation(value = "delete a coupon")
    Mono<Void> delete(int couponId, String operator);
}
