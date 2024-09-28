package com.itsthatjun.ecommerce.service.SMS;

import com.itsthatjun.ecommerce.dto.sms.OnSaleRequest;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PromotionService {

    @ApiOperation("")
    Flux<PromotionSale> getAllPromotionSale();

    @ApiOperation("")
    Flux<Product> getAllPromotionSaleItem();

    @ApiOperation("")
    Flux<Product> getAllFlashSaleItem();

    @ApiOperation("")
    Mono<OnSaleRequest> createListSale(OnSaleRequest request);

    @ApiOperation("")
    Mono<OnSaleRequest> createBrandSale(OnSaleRequest request);

    @ApiOperation("")
    Mono<OnSaleRequest> createCategorySale(OnSaleRequest request);

    @ApiOperation("")
    Mono<OnSaleRequest> updateSaleInfo(OnSaleRequest request);

    @ApiOperation("")
    Mono<OnSaleRequest> updateSalePrice(OnSaleRequest request);

    @ApiOperation("")
    Mono<OnSaleRequest> updateSaleStatus(OnSaleRequest request);

    @ApiOperation("")
    Mono<Void> delete(int promotionSaleId);
}
