package com.itsthatjun.ecommerce.dto.pms.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
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
