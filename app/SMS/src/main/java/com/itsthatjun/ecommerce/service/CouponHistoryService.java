package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.UsedCouponHistory;
import com.itsthatjun.ecommerce.mbg.model.CouponHistory;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface CouponHistoryService {

    @ApiOperation(value = "List all statistic for coupon usage")
    Flux<UsedCouponHistory> listAllCouponUsedBetween(LocalDateTime startTime, LocalDateTime endTime);

    @ApiOperation(value = "Get coupon usage of a user")
    Flux<CouponHistory> getUserCouponUsage(int userId);
}
