package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

public interface SalesService {

    // 0-> not on sale; 1-> is on sale; 2-> flash sale/special sales/clarance/used item
    // promotional sale and flash sale is similar
    // promotion sale is the "normal" discount. flash sale for clearance/used
    @ApiModelProperty(value = "get all promotion list that is on sale")  // TODO: add date interval, active status
    List<PromotionSale> getAllPromotionalSale();

    @ApiModelProperty(value = "get all promotion items on sale")
    List<Product> getAllPromotionalSaleItems();

    @ApiModelProperty(value = "get all sales items")
    List<Product> getAllFlashSaleItems();

    @ApiModelProperty(value = "Generated order, increase sku lock stock")
    void updatePurchase(Map<String, Integer> skuQuantityMap);

    @ApiModelProperty(value = "Generated order and success payment, decrease product stock, decrease sku stock and sku lock stock")
    void updatePurchasePayment(Map<String, Integer> skuQuantityMap);

    @ApiModelProperty(value = "Generated order and success payment and return, increase product stock and sku stock")
    void updateReturn(Map<String, Integer> skuQuantityMap);

    @ApiModelProperty(value = "Generated order and failure payment, decrease sku lock stock")
    void updateFailPayment(Map<String, Integer> skuQuantityMap);
}
