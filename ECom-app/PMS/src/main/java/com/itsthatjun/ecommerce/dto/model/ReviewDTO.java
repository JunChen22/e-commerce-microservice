package com.itsthatjun.ecommerce.dto.model;

import lombok.Data;

import java.util.Date;

@Data
public class ReviewDTO {

    private String memberName;

    private String memberIcon;

    private int star;

    private String tittle;

    private int likes;

    private Integer verified;

    private String content;

    private Date createdAt;

    private Date updatedAt;
}
