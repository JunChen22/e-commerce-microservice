package com.itsthatjun.ecommerce.model;

import com.itsthatjun.ecommerce.model.entity.Coupon;
import com.itsthatjun.ecommerce.model.entity.CouponHistory;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class UsedCouponHistory implements Serializable {

    private Coupon coupon;

    private int userCount;

    private List<CouponHistory> couponHistoryList;
}
