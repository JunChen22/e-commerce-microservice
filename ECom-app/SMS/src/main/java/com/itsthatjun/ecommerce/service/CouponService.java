package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.Coupon;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

public interface  CouponService {

    @ApiOperation("check coupon exist by coupon code")
    Coupon checkCoupon(String couponCode);

    @ApiOperation("Get All coupons that can apply to a product")
    List<Coupon> getCouponForProduct(int productId);

    @ApiOperation("update used coupon count and history")
    void updateUsedCoupon(String code, int orderId, int memberId);

    @ApiOperation("Get discounted amount for all the product affected by the coupon")
    double getDiscountAmount(Map<String, Integer> skuQuantity, String couponCode);
}
