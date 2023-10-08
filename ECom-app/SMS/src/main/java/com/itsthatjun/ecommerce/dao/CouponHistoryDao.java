package com.itsthatjun.ecommerce.dao;

import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

public interface CouponHistoryDao {
    @ApiModelProperty(value = "show number of user used this coupon")
    int couponUserUsageCount(@Param("couponId") int couponId);
}
