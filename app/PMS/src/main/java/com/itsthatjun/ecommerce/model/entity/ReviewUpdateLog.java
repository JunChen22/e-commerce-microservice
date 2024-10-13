package com.itsthatjun.ecommerce.model.entity;

import java.util.Date;

public class ReviewUpdateLog {
    private Integer id;

    private Integer reviewId;

    private String updateAction;

    private String operator;

    private Date createdAt;
}