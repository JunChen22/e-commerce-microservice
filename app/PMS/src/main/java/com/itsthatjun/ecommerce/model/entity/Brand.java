package com.itsthatjun.ecommerce.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("brand")
public class Brand {
    @Id
    private Integer id;

    private String name;

    private String alphabet;

    private Integer status;

    private String logo;
}