package com.itsthatjun.ecommerce.controller;


import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import com.itsthatjun.ecommerce.service.impl.SalesServiceimpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("/sale")
@Api(tags = "Sales related", description = "Item on sale for a set of time")
public class PromotionController {

    private final SalesServiceimpl salesServiceimpl;

    @Autowired
    public PromotionController(SalesServiceimpl salesServiceimpl) {
        this.salesServiceimpl = salesServiceimpl;
    }

    @GetMapping("/AllPromotionSale")
    @ApiOperation("")
    public Flux<PromotionSale> getAllPromotionSale() {
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
