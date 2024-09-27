package com.itsthatjun.ecommerce.dto.pms.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductSkuDTO {

    private String skuCode;

    private String picture;

    private BigDecimal price;

    private BigDecimal promotionPrice;

    private Integer stock;
}
