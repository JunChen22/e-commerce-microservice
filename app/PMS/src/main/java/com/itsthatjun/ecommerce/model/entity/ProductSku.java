package com.itsthatjun.ecommerce.model.entity;

import com.itsthatjun.ecommerce.enums.status.PublishStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("product_sku")
public class ProductSku {
    @Id
    private Integer id;

    private Integer productId;

    private String skuCode;

    private String picture;

    private BigDecimal price;

    private BigDecimal promotionPrice;

    private Integer stock;

    private Integer lowStock;

    private Integer lockStock;

    private Integer unitSold;

    private PublishStatus publishStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}