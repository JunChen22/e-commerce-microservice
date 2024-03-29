package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.OnSale;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.impl.SalesServiceimpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/sale")
@Api(tags = "Sales related", description = "Item on sale for a set of time")
public class PromotionController {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionController.class);

    private final SalesServiceimpl salesServiceimpl;

    @Autowired
    public PromotionController(SalesServiceimpl salesServiceimpl) {
        this.salesServiceimpl = salesServiceimpl;
    }

    @GetMapping("/AllPromotionSale")
    @ApiOperation("")
    public Flux<OnSale> getAllPromotionSale() {
        return salesServiceimpl.getAllPromotionalSale();
    }

    @GetMapping("/AllPromotionSaleItem")
    @ApiOperation("")
    public Flux<Product> getAllPromotionSaleItem() {
        return salesServiceimpl.getAllPromotionalSaleItems();
    }

    @GetMapping("/AllFlashSaleItem")
    @ApiOperation("")
    public Flux<Product> getAllFlashSaleItem() {
        return salesServiceimpl.getAllFlashSaleItems();
    }
}
