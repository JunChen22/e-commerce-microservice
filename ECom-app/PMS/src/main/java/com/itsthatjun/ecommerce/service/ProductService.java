package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;

public interface ProductService {

    @ApiModelProperty(value = "")
    Product getProduct(int id);

    @ApiModelProperty(value = "")
    List<Product> listAllProduct();

    @ApiModelProperty(value = "")
    List<Product> listProduct(int pageNum, int pageSize);

    @ApiModelProperty(value = "Generated order, increase sku lock stock")
    void updatePurchase(Map<String, Integer> skuQuantityMap);

    @ApiModelProperty(value = "Generated order and success payment, decrease product stock, decrease sku stock and sku lock stock")
    void updatePurchasePayment(Map<String, Integer> skuQuantityMap);

    @ApiModelProperty(value = "Generated order and success payment and return, increase product stock and sku stock")
    void updateReturn(Map<String, Integer> skuQuantityMap);

    @ApiModelProperty(value = "Generated order and failure payment, decrease sku lock stock")
    void updateFailPayment(Map<String, Integer> skuQuantityMap);
}
