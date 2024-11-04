package com.itsthatjun.ecommerce.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("product_category")
public class ProductCategory {
    @Id
    private Integer id;

    private String name;

    private Integer parentId;

    private Integer level;
}