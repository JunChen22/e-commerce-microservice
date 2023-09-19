package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.CouponDiscount;
import com.itsthatjun.ecommerce.exceptions.CouponException;
import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.mbg.model.CouponExample;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

public interface  CouponService {

    @ApiOperation("Return discount amount based on item list")
    CouponDiscount checkDiscount(CouponDiscount couponDiscount);

    @ApiOperation("check coupon exist by coupon code")
    Coupon checkCoupon(String couponCode);

    @ApiOperation("Get All coupons that can apply to a product")
    List<Coupon> getCouponForProduct(int productId);

    @ApiOperation("update used coupon count and history")
    void updateUsedCoupon(String code, int orderId, int memberId);

    @ApiOperation("Get discounted amount for all the product affected by the coupon")
    double getDiscountAmount(Map<String, Integer> skuQuantity, String couponCode);

    @ApiOperation(value = "")
    Coupon createCoupon(Coupon newCoupon, Map<String, Integer> skuQuantity);

    @ApiOperation(value = "Get all coupon")
    List<Coupon> getAllCoupon();

    @ApiOperation(value = "Get detail of a coupon by id")
    Coupon getACoupon(int id);

    @ApiOperation(value = "Update coupon info's like discount type, amount, date, count, status, and name")
    Coupon updateCoupon(Coupon updateCoupon, Map<String, Integer> skuMap);

    @ApiOperation(value = "")
    void deleteCoupon(int couponId);
}
