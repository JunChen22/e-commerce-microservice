package com.itsthatjun.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Data
public class CouponDiscount {

    private Map<String, Integer> skuQuantity;

    private BigDecimal discountAmount;

    private String couponCode;

    private String message;
}
