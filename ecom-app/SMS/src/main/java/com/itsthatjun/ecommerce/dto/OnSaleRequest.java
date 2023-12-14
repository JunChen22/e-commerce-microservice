package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import com.itsthatjun.ecommerce.mbg.model.PromotionSaleProduct;
import lombok.Data;

import java.util.List;

@Data
public class OnSaleRequest {
    private PromotionSale promotionSale;
    private int productCategoryId;
    private int brandId;
    private String operator;
    private List<PromotionSaleProduct> onSaleProduct;
}
