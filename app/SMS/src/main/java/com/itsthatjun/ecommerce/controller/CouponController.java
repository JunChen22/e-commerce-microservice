package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.service.impl.CouponServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/coupon")
@Api(tags = "", description = "")
public class CouponController {

    private static final Logger LOG = LoggerFactory.getLogger(CouponController.class);

    private CouponServiceImpl couponService;

    @Autowired
    public CouponController(CouponServiceImpl couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/check")
    @ApiOperation("Check coupon and return discount amount")
    public Mono<Double> checkCoupon(@RequestParam String couponCode, @RequestHeader("X-UserId") int userId) {
        // TODO: currently return total amount, need to change to return individual discount.
        //  might return something like <String, Double> skuDiscount
        return couponService.checkDiscount(couponCode, userId);
    }

    @GetMapping("/admin/listAll")
    @ApiOperation("list all coupons")
    public Flux<Coupon> listAllCoupon() {
        return couponService.getAllCoupon();
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
