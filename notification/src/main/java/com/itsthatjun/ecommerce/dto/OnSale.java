package com.itsthatjun.ecommerce.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OnSale {

    private String name;

    //private String discount;   //   promotionType + amount + discountType +  combined. E.g All +  15 + %  off. or Product 10 $ off

    private Date startTime;

    private Date endTime;
}
