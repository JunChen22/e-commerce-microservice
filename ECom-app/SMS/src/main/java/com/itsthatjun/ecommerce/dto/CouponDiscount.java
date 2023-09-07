package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.mbg.model.Product;

import java.util.List;

public class CouponDiscount {

    // need to get quantity and sku if there's difference
    List<Product> productList;
    double discountAmount;
    String message;
}
