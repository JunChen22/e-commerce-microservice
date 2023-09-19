package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.UsedCouponHistory;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;


public interface CouponHistoryService {

    @ApiOperation(value = "")
    Flux<UsedCouponHistory> getAllCouponUsed();

    @ApiOperation(value = "")
    Flux<UsedCouponHistory> getUserCoupon(int id);
}
