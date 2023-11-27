package com.itsthatjun.ecommerce.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

@Data
public class CouponDiscount {
    @ApiModelProperty("check price discounted by the coupon")
    Map<String, Integer> skuQuantity;
    double discountAmount;
    String couponCode;
    String message;
}
