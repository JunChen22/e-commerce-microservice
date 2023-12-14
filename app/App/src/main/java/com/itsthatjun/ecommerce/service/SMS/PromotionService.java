package com.itsthatjun.ecommerce.service.SMS;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;

public interface PromotionService {

    @ApiOperation("All sales including promotional sale(regular discount) and flash sale(could clearance or limited time discount")
    public Flux<PromotionSale> getAllPromotionSale();

    @ApiOperation("get all the item that is on regular sale discount")
    public Flux<Product> getAllPromotionSaleItem();

    @ApiOperation("get all item that is on short term sale like clearance or special sale")
    public Flux<Product> getAllFlashSaleItem();
}
