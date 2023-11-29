package com.itsthatjun.ecommerce.dto.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReturnItemDTO {

    private String productSku;

    private BigDecimal purchasedPrice;

    private Integer quantity;
}
