package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.model.entity.PromotionSale;
import com.itsthatjun.ecommerce.model.entity.PromotionSaleProduct;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class OnSaleRequest implements Serializable {

    private PromotionSale promotionSale;

    private int productCategoryId;

    private int brandId;

    private String operator;

    private List<PromotionSaleProduct> onSaleProduct;
}
