package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import com.itsthatjun.ecommerce.mbg.model.PromotionSaleProduct;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class OnSaleRequest {
    private PromotionSale promotionSale;
    private PromotionType promotionType;
    private DiscountType discountType;
    private double discountAmount;
    private int productCategoryId;
    private int brandId;
    private String operator;
    private List<PromotionSaleProduct> onSaleProduct;
}
