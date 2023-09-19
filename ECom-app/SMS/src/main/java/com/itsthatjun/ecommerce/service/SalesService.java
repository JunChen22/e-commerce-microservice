package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.OnSaleRequest;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SalesService {

    // 0-> not on sale; 1-> is on sale; 2-> flash sale/special sales/clarance/used item
    // promotional sale and flash sale is similar
    // promotion sale is the "normal" discount. flash sale for clearance/used
    @ApiOperation(value = "get all promotion list that is on sale")  // TODO: add date interval, active status
    Flux<PromotionSale> getAllPromotionalSale();

    @ApiOperation(value = "get all promotion items on sale")
    Flux<Product> getAllPromotionalSaleItems();

    @ApiOperation(value = "get all flash sales items")
    Flux<Product> getAllFlashSaleItems();

    @ApiOperation(value = "Update price, revert back to original after sale limit is reached")
    void updateSaleLimitPrice();

    @ApiOperation(value = "Update price, revert back to original price after time limit")
    void updateSaleTimeFramePrice();

    // ===================== admin =====================
    @ApiOperation(value = "create sale on list of items")
    Mono<PromotionSale> createListSale(OnSaleRequest request, String operator);

    /* TODO: create sepearate sale creation, currently is passed in after they are searched.
    @ApiOperation(value = "create sales based on brand name")
    Mono<PromotionSale> createBrandSale(OnSaleRequest request, String operator);

    @ApiOperation(value = "create sales based on product category")
    Mono<PromotionSale> createCategorySale(OnSaleRequest request, String operator);
     */
    @ApiOperation(value = "Update info like name, sale type and time, non-price affecting")
    Mono<PromotionSale> updateSaleInfo(OnSaleRequest updateSaleRequest, String operator);

    @ApiOperation(value = "Update sale discount percent or fixed amount. price affecting")
    Mono<PromotionSale> updateSalePrice(OnSaleRequest updateSaleRequest, String operator);

    @ApiOperation(value = "Update sale to be online or off line, price affecting")
    Mono<PromotionSale> updateSaleStatus(OnSaleRequest updateSaleRequest, String operator);

    @ApiOperation(value = "delete")
    void delete(int promotionSaleId, String operator);
}
