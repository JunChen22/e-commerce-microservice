package com.itsthatjun.ecommerce.dto.oms.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItemDTO {

    private String productName;

    private String productSku;

    private String productPic;

    private Integer quantity;

    private BigDecimal price;
}
