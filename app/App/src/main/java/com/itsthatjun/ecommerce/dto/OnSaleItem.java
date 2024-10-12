package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.model.Brand;
import com.itsthatjun.ecommerce.model.Product;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OnSaleItem {

    private Product product;

    private Brand brand;

    private BigDecimal discountAmount;

    private int numberAvailable;

    private Date timeStart;

    private Date timeEnd;
}
