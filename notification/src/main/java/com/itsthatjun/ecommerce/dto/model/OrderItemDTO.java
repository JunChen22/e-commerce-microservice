package com.itsthatjun.ecommerce.dto.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
