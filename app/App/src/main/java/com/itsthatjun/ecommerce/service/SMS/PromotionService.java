package com.itsthatjun.ecommerce.service.SMS;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;

public interface PromotionService {

    @ApiOperation("List all sales including promotional sale(regular discount) and flash sale(could clearance or limited time discount")
    Flux<PromotionSale> listAllPromotionSale();

    @ApiOperation("List all the item that is on regular sale discount")
    Flux<Product> listAllPromotionSaleItem();

    @ApiOperation("List all item that is on short term sale like clearance or special sale")
    Flux<Product> listAllFlashSaleItem();
}
