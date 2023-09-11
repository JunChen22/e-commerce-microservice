package com.itsthatjun.ecommerce.service.SMS;

import com.itsthatjun.ecommerce.dto.sms.UsedCouponHistory;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface CouponHistoryService {

    @ApiOperation(value = "")
    List<UsedCouponHistory> getAllCouponUsed();

    @ApiOperation(value = "")
    List<UsedCouponHistory> getUserCoupon(int id);
}
