package com.itsthatjun.ecommerce.model.entity;

import java.math.BigDecimal;
import java.util.Date;

public class ProductUpdateLog {
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

    private Date createdAt;
}