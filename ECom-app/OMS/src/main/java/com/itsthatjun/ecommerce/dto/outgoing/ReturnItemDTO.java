package com.itsthatjun.ecommerce.dto.outgoing;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReturnItemDTO {

    private String productSku;

    private BigDecimal purchasedPrice;

    private Integer quantity;
}
