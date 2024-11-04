package com.itsthatjun.ecommerce.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("product_attribute_category")
public class ProductAttributeCategory {
    @Id
    private Integer id;

    private String name;

    private Integer attributeAmount;
}