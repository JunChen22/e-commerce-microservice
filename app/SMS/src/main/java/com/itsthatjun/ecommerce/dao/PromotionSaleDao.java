package com.itsthatjun.ecommerce.dao;

import com.itsthatjun.ecommerce.dto.OnSale;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public interface PromotionSaleDao {

    @ApiModelProperty(value = "get all promotion sales going on")
    List<OnSale> getOnPromotionSale();
}
