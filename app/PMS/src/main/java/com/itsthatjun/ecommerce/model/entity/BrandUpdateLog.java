package com.itsthatjun.ecommerce.model.entity;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class BrandUpdateLog {
    @Id
    private Integer id;

    private Integer brandId;

    private String updateAction;

    private String operator;

    private Date createdAt;
}