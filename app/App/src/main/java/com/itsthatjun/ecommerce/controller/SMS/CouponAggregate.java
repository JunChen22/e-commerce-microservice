package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.service.SMS.impl.CouponServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@RestController
@Tag(name = "Coupon controller", description = "Coupon controller")
@RequestMapping("/coupon")
public class CouponAggregate {

    private final CouponServiceImpl couponService;

    @Autowired
    public CouponAggregate(CouponServiceImpl couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "Check coupon and return discount amount", description = "Check coupon and return discount amount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Check coupon and return discount amount"),
            @ApiResponse(responseCode = "404", description = "Coupon not found")
    })
    @GetMapping("/check")
    public Mono<BigDecimal> checkCoupon(@RequestParam String couponCode) {
        return couponService.checkCoupon(couponCode);
    }
}
