package com.itsthatjun.ecommerce.model.entity;

import java.math.BigDecimal;

public class ProductSku {
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

    private Integer status;
}