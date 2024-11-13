package com.itsthatjun.ecommerce.dto.oms.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ReturnItemDTO {

    private String productSku;

    private BigDecimal purchasedPrice;

    private Integer quantity;
}
