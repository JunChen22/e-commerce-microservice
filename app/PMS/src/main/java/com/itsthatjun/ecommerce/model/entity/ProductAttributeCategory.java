package com.itsthatjun.ecommerce.model.entity;

import org.springframework.data.annotation.Id;

public class ProductAttributeCategory {
    @Id
    private Integer id;

    private String name;

    private Integer attributeAmount;
}