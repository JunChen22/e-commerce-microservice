package com.itsthatjun.ecommerce.dto.oms.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {

    private String productPic;

    private String productName;

    private String productBrand;

    private String productSn;

    private BigDecimal productPrice;

    private Integer productQuantity;

    private String productSkuCode;

    private BigDecimal promotionAmount;

    private BigDecimal couponAmount;

    private BigDecimal realAmount;
}
