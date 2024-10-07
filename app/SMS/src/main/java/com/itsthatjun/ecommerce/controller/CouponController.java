package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.service.impl.CouponServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@RequestMapping("/coupon")
@Tag(name = "", description = "")
public class CouponController {

    private CouponServiceImpl couponService;

    @Autowired
    public CouponController(CouponServiceImpl couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/check")
    @ApiOperation("Check coupon and return discount amount")
    public Mono<BigDecimal> checkCoupon(@RequestParam String couponCode, @RequestHeader("X-UserId") int userId) {
        // TODO: currently return total amount, need to change to return individual discount.
        //  might return something like <String, Double> skuDiscount
        return couponService.checkDiscount(couponCode, userId);
    }

    @GetMapping("/admin/listAll")
    @ApiOperation("list all coupons")
    public Flux<Coupon> listAllCoupon() {
        return couponService.listAllCoupon();
    }

    @GetMapping("/admin/{couponId}")
    @ApiOperation("list all coupons")
    public Mono<Coupon> CouponDetail(@PathVariable int couponId) {
        // TODO: show coupon and all of affected product
        //     if it's all then just say all
        return couponService.getACoupon(couponId);
    }

    @GetMapping("/admin/product/{productId}")
    @ApiOperation("list all coupons")
    public Flux<Coupon> getCouponForProduct(@PathVariable int productId) {
        return couponService.getCouponForProduct(productId);
    }
}
