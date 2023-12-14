package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.mbg.model.CouponHistory;
import lombok.Data;

import java.util.List;

@Data
public class UsedCouponHistory {
    private Coupon coupon;
    private int userCount;
    private List<CouponHistory> couponHistoryList;
}
