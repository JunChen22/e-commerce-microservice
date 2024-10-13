package com.itsthatjun.ecommerce.model.entity;

import org.springframework.data.annotation.Id;

public class ProductAttribute {
    @Id
    private Integer id;

    private String skuCode;

    private Integer productId;

    private Integer attributeTypeId;

    private String attributeValue;

    private String attributeUnit;
}