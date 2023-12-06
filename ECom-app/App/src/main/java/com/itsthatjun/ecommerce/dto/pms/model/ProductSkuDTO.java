package com.itsthatjun.ecommerce.dto.pms.model;

import lombok.Data;

@Data
public class ProductSkuDTO {

    private String skuCode;

    private String picture;

    private double price;

    private double promotionPrice;

    private Integer stock;
}
