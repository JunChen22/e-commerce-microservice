package com.itsthatjun.ecommerce.service.SMS;

import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface CouponService {

    @ApiOperation(value = "")
    Coupon createCoupon(Coupon newCoupon);

    @ApiOperation(value = "")
    List<Coupon> getAllCoupon();

    @ApiOperation(value = "")
    Coupon getACoupon(int id);

    @ApiOperation(value = "")
    List<Coupon> getCouponForProduct(int productId);

    @ApiOperation(value = "")
    Coupon updateCoupon(Coupon updateCoupon);

    @ApiOperation(value = "")
    void deleteCoupon(int couponId);
}
