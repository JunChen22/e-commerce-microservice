package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
import com.itsthatjun.ecommerce.service.SMS.impl.PromotionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

//    @Operation(summary = "All sales including promotional sale(regular discount) and flash sale(could clearance or limited time discount")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "All sales including promotional sale(regular discount) and flash sale(could clearance or limited time discount"),
//            @ApiResponse(responseCode = "404", description = "No sale found")})
//    @GetMapping("/listAllPromotionSale")
//    public Flux<PromotionSale> listAllPromotionSale() {
//        Flux<PromotionSale> promotionSaleFlux = promotionService.listAllPromotionSale();
//        return promotionSaleFlux;
//    }

    @Operation(summary = "get all the item that is on regular sale discount", description = "get all the item that is on regular sale discount")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all the item that is on regular sale discount"),
            @ApiResponse(responseCode = "404", description = "No item found")})
    @GetMapping("/AllPromotionSaleItem")
    public Flux<ProductDTO> getAllPromotionSaleItem() {
        return promotionService.listAllPromotionSaleItem();
    }

    @Operation(summary = "get all item that is on short term sale like clearance or special sale", description = "get all item that is on short term sale like clearance or special sale")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all item that is on short term sale like clearance or special sale"),
            @ApiResponse(responseCode = "404", description = "No item found")})
    @GetMapping("/AllFlashSaleItem")
    public Flux<ProductDTO> getAllFlashSaleItem() {
        return promotionService.listAllFlashSaleItem();
    }
}
