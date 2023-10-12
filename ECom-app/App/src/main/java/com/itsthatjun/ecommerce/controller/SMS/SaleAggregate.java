package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import com.itsthatjun.ecommerce.service.SMS.impl.PromotionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.logging.Level.FINE;

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
    @Cacheable(value = "promotionSale", key = "'getAllPromotionSale'")
    @ApiOperation("All sales including promotional sale(regular discount) and flash sale(could clearance or limited time discount")
    public List<PromotionSale> getAllPromotionSale() {
        Flux<PromotionSale> promotionSaleFlux = promotionService.getAllPromotionSale();
        List<PromotionSale> promotionSaleList = promotionSaleFlux.collectList().block();

        return promotionSaleList;
    }

    @GetMapping("/AllPromotionSaleItem")
    @Cacheable(value = "promotionSaleCache", key = "'getAllPromotionSaleItem'")
    @ApiOperation("get all the item that is on regular sale discount")
    public List<Product> getAllPromotionSaleItem() {
        Flux<Product> productFlux = promotionService.getAllPromotionSaleItem();
        List<Product> productList = productFlux.collectList().block();

        return productList;
    }

    @GetMapping("/AllFlashSaleItem")
    @Cacheable(value = "flashSaleCache", key = "'getAllFlashSaleItem'")
    @ApiOperation("get all item that is on short term sale like clearance or special sale")
    public List<Product> getAllFlashSaleItem() {
        Flux<Product> productFlux = promotionService.getAllFlashSaleItem();
        List<Product> productList = productFlux.collectList().block();

        return productList;
    }
}
