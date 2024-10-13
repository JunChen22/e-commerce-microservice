package com.itsthatjun.ecommerce.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Table("product")
public class Product {
    @Id
    private Integer id;

    private Integer brandId;

    private String brandName;

    private String name;

    private String slug;

    private Integer categoryId;

    private String categoryName;

    private Integer attributeCategoryId;

    private String sn;

    private Integer newStatus;

    private Integer recommendStatus;

    private Integer verifyStatus;

    private String subTitle;

    private String coverPicture;

    private Integer pictureAlbum;

    private String description;

    private BigDecimal originalPrice;

    private Integer onSaleStatus;

    private BigDecimal salePrice;

    private Integer stock;

    private Integer lowStock;

    private Integer unitSold;

    private BigDecimal weight;

    private String keywords;

    private String detailTitle;

    private String detailDesc;

    private Integer descriptionAlbumId;

    private Integer deleteStatus;

    private Integer publishStatus;

    private LocalDateTime createdAt;

    private String note;
}