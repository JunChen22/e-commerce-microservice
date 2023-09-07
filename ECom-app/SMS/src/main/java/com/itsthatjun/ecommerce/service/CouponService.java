package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.mbg.model.OrderItem;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public interface CouponService {

    @ApiModelProperty("check coupon exist by coupon code")
    Coupon checkCoupon(String couponCode);

    @ApiModelProperty("Get All coupons that can apply to a product")
    List<Coupon> getCouponForProduct(int productId);

    @ApiModelProperty("update used coupon count and history")
    void updateUsedCoupon(String code, int orderId, int memberId);

    @ApiModelProperty("Get discounted amount for all the product affected by the coupon")
    double getDiscountAmount(List<OrderItem> items, String couponCode);
}
