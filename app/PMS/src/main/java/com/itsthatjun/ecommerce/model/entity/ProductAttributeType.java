package com.itsthatjun.ecommerce.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("product_attribute_type")
public class ProductAttributeType {
    @Id
    private Integer id;

    private Integer attributeCategoryId;

    private String name;
}