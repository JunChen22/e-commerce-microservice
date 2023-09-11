package com.itsthatjun.ecommerce.service.SMS;

import com.itsthatjun.ecommerce.dto.sms.OnSaleRequest;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface SalesService {

    // 0-> not on sale; 1-> is on sale; 2-> flash sale/special sales/clarance/used item
    // promotional sale and flash sale is similar
    // promotion sale is the "normal" discount. flash sale for clearance/used
    @ApiOperation(value = "get all promotion items on sale")  // TODO: add date interval, active status
    List<PromotionSale> getAllPromotionalSale();

    @ApiOperation(value = "get all promotion items on sale")
    List<Product> getAllPromotionalSaleItems();

    @ApiOperation(value = "get all sales items")
    List<Product> getAllFlashSaleItems();

    @ApiOperation(value = "create sale on list of items")
    List<Product> createList(OnSaleRequest request);

    @ApiOperation(value = "create sales based on brand name")
    List<Product> createBrandSale(OnSaleRequest request);

    @ApiOperation(value = "create sales based on product category")
    List<Product> createCategorySale(OnSaleRequest request);

    @ApiOperation(value = "")
    OnSaleRequest updateStatus(OnSaleRequest updateSaleRequest);

    @ApiOperation(value = "")
    OnSaleRequest upateSaleDate(OnSaleRequest updateSaleRequest);

    @ApiOperation(value = "")
    OnSaleRequest updateSaleAmount(OnSaleRequest updateSaleRequest);

    @ApiOperation(value = "")
    OnSaleRequest updateSaleLimt(OnSaleRequest updateSaleRequest);

    @ApiOperation(value = "update promotion sale to clarance sale vice versa")
    OnSaleRequest updateSaleType(OnSaleRequest updateSaleRequest);

    @ApiOperation(value = "delete")
    void delete(int promotionSaleId);
}
