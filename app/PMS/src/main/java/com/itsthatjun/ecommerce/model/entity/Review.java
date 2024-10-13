package com.itsthatjun.ecommerce.model.entity;

import java.math.BigDecimal;
import java.util.Date;

public class Review {
    private Integer id;

    private Integer productId;

    private Integer memberId;

    private String memberName;

    private String memberIcon;

    private Integer star;

    private String tittle;

    private BigDecimal likes;

    private Integer verified;

    private String content;

    private Date createdAt;

    private Date updatedAt;
}