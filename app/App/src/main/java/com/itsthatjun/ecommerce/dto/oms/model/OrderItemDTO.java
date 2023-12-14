package com.itsthatjun.ecommerce.dto.oms.model;

import lombok.Data;

@Data
public class OrderItemDTO {

    private String productPic;

    private String productName;

    private String productBrand;

    private String productSn;

    private double productPrice;

    private Integer productQuantity;

    private String productSkuCode;

    private double promotionAmount;

    private double couponAmount;

    private double realAmount;
}
