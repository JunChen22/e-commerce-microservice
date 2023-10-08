package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.UsedCouponHistory;
import com.itsthatjun.ecommerce.mbg.model.CouponHistory;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;


public interface CouponHistoryService {

    @ApiOperation(value = "Get statistic for coupon usage")
    Flux<UsedCouponHistory> getAllCouponUsed();

    @ApiOperation(value = "Get coupon usage of a user")
    Flux<CouponHistory> getUserCouponUsage(int userId);
}
