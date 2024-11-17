package com.itsthatjun.ecommerce.model;

import com.itsthatjun.ecommerce.model.entity.Coupon;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
public class CouponSale implements Serializable {

    private Coupon coupon;

    /**
     * affected product skuId
     * key: skuId
     * value: quantity
     */
    private Map<String, Integer> skuQuantity;
}
