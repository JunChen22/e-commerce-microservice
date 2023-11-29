package com.itsthatjun.ecommerce.dto.model;

import lombok.Data;

import java.util.Date;

@Data
public class ProductDTO {

    private String brandName;

    private String name;

    private String categoryName;

    private String sn;

    private String subTitle;

    private String coverPicture;

    private String description;

    private double originalPrice;

    private double salePrice;

    private int stock;

    private double weight;

    private String keywords;

    private String detailTitle;

    private String detailDesc;

    private Date createdAt;
}
