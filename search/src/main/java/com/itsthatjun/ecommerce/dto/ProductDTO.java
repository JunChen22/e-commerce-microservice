package com.itsthatjun.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class ProductDTO implements Serializable {

    private String brandName;

    private String name;

    private String categoryName;

    private String sn;

    private String subTitle;

    private String coverPicture;

    private String description;

    private BigDecimal originalPrice;

    private BigDecimal salePrice;

    private int stock;

    private double weight;

    private String keywords;

    private String detailTitle;

    private String detailDesc;

    private Date createdAt;
}
