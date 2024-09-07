package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import com.itsthatjun.ecommerce.service.SMS.impl.PromotionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@Api(tags = "Sale controller", description = "Sale controller")
@RequestMapping("/sale")
public class SaleAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(SaleAggregate.class);

    private PromotionServiceImpl promotionService;

    @Autowired
    public SaleAggregate(PromotionServiceImpl promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/AllPromotionSale")
    @ApiOperation("All sales including promotional sale(regular discount) and flash sale(could clearance or limited time discount")
    public Flux<PromotionSale> getAllPromotionSale() {
        Flux<PromotionSale> promotionSaleFlux = promotionService.getAllPromotionSale();
        return promotionSaleFlux;
    }

    @GetMapping("/AllPromotionSaleItem")
    @ApiOperation("get all the item that is on regular sale discount")
    public Flux<Product> getAllPromotionSaleItem() {
        Flux<Product> productFlux = promotionService.getAllPromotionSaleItem();
        return productFlux;
    }

    @GetMapping("/AllFlashSaleItem")
    @ApiOperation("get all item that is on short term sale like clearance or special sale")
    public Flux<Product> getAllFlashSaleItem() {
        Flux<Product> productFlux = promotionService.getAllFlashSaleItem();
        return productFlux;
    }
}
