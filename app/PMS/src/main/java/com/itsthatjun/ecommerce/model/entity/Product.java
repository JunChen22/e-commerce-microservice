package com.itsthatjun.ecommerce.model.entity;

import com.itsthatjun.ecommerce.enums.ProductCondition;
import com.itsthatjun.ecommerce.enums.status.PublishStatus;
import com.itsthatjun.ecommerce.enums.status.RecommendationStatus;
import com.itsthatjun.ecommerce.enums.status.VerificationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

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

    private ProductCondition conditionStatus;

    private RecommendationStatus recommendStatus;

    private VerificationStatus verifyStatus;

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

    private Boolean deleteStatus;

    private PublishStatus publishStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String note;
}