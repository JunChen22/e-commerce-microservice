package com.itsthatjun.ecommerce.service.SMS;

import com.itsthatjun.ecommerce.dto.SMS.UsedCouponHistory;
import com.itsthatjun.ecommerce.mbg.model.Coupon;
import io.swagger.annotations.ApiOperation;

import java.util.List;
import java.util.Map;

public interface CouponHistoryService {

    @ApiOperation(value = "")
    List<UsedCouponHistory> getAllCouponUsed();

    @ApiOperation(value = "")
    List<UsedCouponHistory> getUserCoupon(int id);
}
