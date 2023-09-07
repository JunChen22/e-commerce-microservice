package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.mbg.mapper.CouponMapper;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.impl.CouponServiceImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coupon")
public class CouponController {

    private final CouponMapper couponMapper;

    private CouponServiceImpl couponService;

    @Autowired
    public CouponController(CouponMapper couponMapper, CouponServiceImpl couponService) {
        this.couponMapper = couponMapper;
        this.couponService = couponService;
    }

    @GetMapping("/check")
    @ApiOperation("")
    public double checkCoupon(List<Product> products, String coupon) {

        return 0.0;
    }
}
