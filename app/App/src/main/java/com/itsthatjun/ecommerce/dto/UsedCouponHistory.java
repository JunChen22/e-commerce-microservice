package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.model.Coupon;
import com.itsthatjun.ecommerce.model.CouponHistory;
import lombok.Data;

import java.util.List;

@Data
public class UsedCouponHistory {

    private Coupon coupon;

    private List<CouponHistory> couponHistoryList;
}
