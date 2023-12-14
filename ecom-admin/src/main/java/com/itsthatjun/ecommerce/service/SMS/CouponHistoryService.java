package com.itsthatjun.ecommerce.service.SMS;

import com.itsthatjun.ecommerce.dto.sms.UsedCouponHistory;
import com.itsthatjun.ecommerce.mbg.model.CouponHistory;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;

public interface CouponHistoryService {

    @ApiOperation(value = "return all the coupon used between two time")
    Flux<UsedCouponHistory> couponUsed();

    @ApiOperation(value = "shows how many coupon(amount saved) a user used")
    Flux<CouponHistory> getUserCoupon(int userId);
}
