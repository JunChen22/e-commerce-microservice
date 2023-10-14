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
    Mono<OnSaleRequest> createListSale(OnSaleRequest request, String operator);

    @ApiOperation("")
    Mono<OnSaleRequest> createBrandSale(OnSaleRequest request, String operator);

    @ApiOperation("")
    Mono<OnSaleRequest> createCategorySale(OnSaleRequest request, String operator);

    @ApiOperation("")
    Mono<OnSaleRequest> updateSaleInfo(OnSaleRequest request, String operator);

    @ApiOperation("")
    Mono<OnSaleRequest> updateSalePrice(OnSaleRequest request, String operator);

    @ApiOperation("")
    Mono<OnSaleRequest> updateSaleStatus(OnSaleRequest request, String operator);

    @ApiOperation("")
    Mono<Void> delete(int promotionSaleId, String operator);
}
