package com.itsthatjun.ecommerce.dto.model;

import lombok.Data;

import java.util.Map;

@Data
public class ProductSkuDTO {

    private String skuCode;

    private String picture;

    private double price;

    private double promotionPrice;

    private Integer stock;

    private Map<String, String> attribute;
}
