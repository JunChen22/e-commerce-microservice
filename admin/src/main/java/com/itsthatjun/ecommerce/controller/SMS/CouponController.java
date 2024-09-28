package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.dto.sms.CouponSale;
import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.service.SMS.impl.CouponServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/coupon")
@PreAuthorize("hasRole('ROLE_admin-sale')")
@Api(tags = "Coupon related", description = "CRUD coupon by admin with right roles/permission")
public class CouponController {

    private static final Logger LOG = LoggerFactory.getLogger(CouponController.class);

    private final CouponServiceImpl couponService;

    @Autowired
    public CouponController(CouponServiceImpl couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "return all working non-expired coupon")
    public Flux<Coupon> listAll() {
        return couponService.listAll();
    }

    @GetMapping("/{couponId}")
    @ApiOperation(value = "Get coupon detail and related products")
    public Mono<Coupon> couponDetail(@PathVariable int couponId) {
        return couponService.list(couponId);
    }

    @GetMapping("/product/{productId}")
    @ApiOperation(value = "Get coupons that works with a product")
    public Flux<Coupon> getCouponForProduct(@PathVariable int productId) {
        return couponService.getCouponForProduct(productId);
    }

    @PostMapping("/create")
    @ApiOperation(value = "create a coupon")
    public Mono<CouponSale> create(@RequestBody CouponSale couponSale) {
        return couponService.create(couponSale);
    }

    @PostMapping("/update")
    @ApiOperation(value = "update a coupon")
    public Mono<CouponSale> update(@RequestBody CouponSale updatedCouponSale) {
        return couponService.update(updatedCouponSale);
    }

    @DeleteMapping("/delete/{couponId}")
    @ApiOperation(value = "delete a coupon")
    public Mono<Void> delete(@PathVariable int couponId) {
        return couponService.delete(couponId);
    }
}
