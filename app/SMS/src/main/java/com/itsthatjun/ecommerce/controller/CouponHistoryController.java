package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.UsedCouponHistory;
import com.itsthatjun.ecommerce.mbg.model.CouponHistory;
import com.itsthatjun.ecommerce.service.impl.CouponHistoryServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/coupon/history")
@Api(tags = "", description = "")
public class CouponHistoryController {

    private static final Logger LOG = LoggerFactory.getLogger(CouponHistoryController.class);

    private CouponHistoryServiceImpl historyService;

    @Autowired
    public CouponHistoryController(CouponHistoryServiceImpl historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/getAllUsedCoupon")
    @ApiOperation(value = "return all the coupon used between two time")
    public Flux<UsedCouponHistory> couponUsed() {
        return historyService.getAllCouponUsed();
    }

    @GetMapping("/getUserCoupon/{userId}")
    @ApiOperation(value = "shows how many coupon(amount saved) a user used")
    public Flux<CouponHistory> getUserCoupon(@PathVariable int userId) {
        return historyService.getUserCouponUsage(userId);
    }
}
