package com.itsthatjun.ecommerce.dto.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
public class ProductSkuDTO {

    private String skuCode;

    private String picture;

    private BigDecimal price;

    private BigDecimal promotionPrice;

    private Integer stock;

    private Map<String, String> attribute;
}
