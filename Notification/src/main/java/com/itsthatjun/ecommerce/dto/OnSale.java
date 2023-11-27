package com.itsthatjun.ecommerce.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OnSale {
    private String name;
    //private String discount;   //   promotionType + amount + discountType +  combined. E.g All +  15 + %  off. or Product 10 $ off
    private Date startTime;
    private Date endTime;
}
