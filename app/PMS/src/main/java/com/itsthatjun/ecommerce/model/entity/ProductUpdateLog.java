package com.itsthatjun.ecommerce.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("product_update_log")
public class ProductUpdateLog {
    @Id
    private Integer id;

    private Integer productId;

    private BigDecimal priceOld;

    private BigDecimal priceNew;

    private BigDecimal salePriceOld;

    private BigDecimal salePriceNew;

    private Integer oldStock;

    private Integer addedStock;

    private Integer totalStock;

    private String updateAction;

    private String operator;

    private LocalDateTime createdAt;
}