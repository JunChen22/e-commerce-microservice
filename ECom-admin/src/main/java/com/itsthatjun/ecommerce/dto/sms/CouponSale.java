package com.itsthatjun.ecommerce.dto.sms;

import com.itsthatjun.ecommerce.mbg.model.Coupon;
import lombok.Data;

import java.util.Map;

@Data
public class CouponSale {
    private Integer couponId;
    private UpdateType updateType;
    private PromotionType promotionType;  //-- discount on 0-> all, 1 -> specific brand,  2-> specific category , 3-> specific item(s)
    private DiscountType discountType;   // 0 -> by amount , 1->  by percent off amount numeric,
    private Coupon coupon;
    private Map<String, Integer> skuQuantity;
}
