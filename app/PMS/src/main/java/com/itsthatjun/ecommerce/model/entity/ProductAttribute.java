package com.itsthatjun.ecommerce.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("product_attribute")
public class ProductAttribute {
    @Id
    private Integer id;

    private String skuCode;

    private Integer productId;

    private Integer attributeTypeId;

    private String attributeValue;

    private String attributeUnit;
}