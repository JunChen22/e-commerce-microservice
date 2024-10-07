package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.UsedCouponHistory;
import com.itsthatjun.ecommerce.mbg.model.CouponHistory;
import com.itsthatjun.ecommerce.service.impl.CouponHistoryServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/coupon/history")
@Tag(name = "", description = "")
public class CouponHistoryController {

    private CouponHistoryServiceImpl historyService;

    @Autowired
    public CouponHistoryController(CouponHistoryServiceImpl historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/listAllUsedCoupon")
    @ApiOperation(value = "list all the coupon used between two time")
    public Flux<UsedCouponHistory> listAllCouponUsed(
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
            return historyService.listAllCouponUsedBetween(startTime, endTime);
    }

    @GetMapping("/getUserCoupon/{userId}")
    @ApiOperation(value = "shows how many coupon(amount saved) a user used")
    public Flux<CouponHistory> getUserCoupon(@PathVariable int userId) {
        return historyService.getUserCouponUsage(userId);
    }
}
