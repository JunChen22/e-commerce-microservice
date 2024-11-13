package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.OnSale;
import com.itsthatjun.ecommerce.service.impl.SaleServiceimpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/sale")
@Tag(name = "Sales related", description = "Item on sale for a set of time")
public class PromotionController {

    private final SaleServiceimpl salesServiceimpl;

    @Autowired
    public PromotionController(SaleServiceimpl salesServiceimpl) {
        this.salesServiceimpl = salesServiceimpl;
    }
//
//    @GetMapping("/AllPromotionSale")
//    @ApiOperation("")
//    public Flux<OnSale> getAllPromotionSale() {
//        return salesServiceimpl.getAllPromotionalSale();
//    }
//
//    @GetMapping("/AllPromotionSaleItem")
//    @ApiOperation("")
//    public Flux<Product> getAllPromotionSaleItem() {
//        return salesServiceimpl.getAllPromotionalSaleItems();
//    }
//
//    @GetMapping("/AllFlashSaleItem")
//    @ApiOperation("")
//    public Flux<Product> getAllFlashSaleItem() {
//        return salesServiceimpl.getAllFlashSaleItems();
//    }
}
