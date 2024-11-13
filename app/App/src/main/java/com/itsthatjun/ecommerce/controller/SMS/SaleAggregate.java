package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
import com.itsthatjun.ecommerce.service.SMS.impl.PromotionServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@Tag(name = "Sale controller", description = "Sale controller")
@RequestMapping("/sale")
public class SaleAggregate {

    private PromotionServiceImpl promotionService;

    @Autowired
    public SaleAggregate(PromotionServiceImpl promotionService) {
        this.promotionService = promotionService;
    }

//    @GetMapping("/listAllPromotionSale")
//    @ApiOperation("All sales including promotional sale(regular discount) and flash sale(could clearance or limited time discount")
//    public Flux<PromotionSale> listAllPromotionSale() {
//        Flux<PromotionSale> promotionSaleFlux = promotionService.listAllPromotionSale();
//        return promotionSaleFlux;
//    }

    @GetMapping("/AllPromotionSaleItem")
    @ApiOperation("get all the item that is on regular sale discount")
    public Flux<ProductDTO> getAllPromotionSaleItem() {
        return promotionService.listAllPromotionSaleItem();
    }

    @GetMapping("/AllFlashSaleItem")
    @ApiOperation("get all item that is on short term sale like clearance or special sale")
    public Flux<ProductDTO> getAllFlashSaleItem() {
        return promotionService.listAllFlashSaleItem();
    }
}
