package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.dto.sms.OnSaleRequest;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import com.itsthatjun.ecommerce.service.SMS.impl.PromotionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sale")
@PreAuthorize("hasRole('ROLE_admin-sale')")
@Api(tags = "Sales related", description = "Item on sale for a set of time")
public class PromotionController {

    private static final Logger LOG = LoggerFactory.getLogger(PromotionController.class);

    private final PromotionServiceImpl promotionService;

    @Autowired
    public PromotionController(PromotionServiceImpl promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/AllPromotionSale")
    @ApiOperation("All the sales that is going on")
    public Flux<PromotionSale> getAllPromotionSale() {
        return promotionService.getAllPromotionSale();
    }

    @GetMapping("/AllPromotionSaleItem")
    @ApiOperation("Items that are on regular sale")
    public Flux<Product> getAllPromotionSaleItem() {
        return promotionService.getAllPromotionSaleItem();
    }

    @GetMapping("/AllFlashSaleItem")
    @ApiOperation("Items that are like flash sale, clearance sale that needs to go quick")
    public Flux<Product> getAllFlashSaleItem() {
        return promotionService.getAllFlashSaleItem();
    }

    @PostMapping("/createList")
    @ApiOperation("")
    public Mono<OnSaleRequest> createList(@RequestBody OnSaleRequest request) {
        return promotionService.createListSale(request);
    }

    @PostMapping("/createBrandSale")
    @ApiOperation("")
    public Mono<OnSaleRequest> createBrandSale(@RequestBody OnSaleRequest request) {
        return promotionService.createBrandSale(request);
    }

    @PostMapping("/createCategorySale")
    @ApiOperation("")
    public Mono<OnSaleRequest> createCategorySale(@RequestBody OnSaleRequest request) {
        return promotionService.createCategorySale(request);
    }

    @PostMapping("/updateInfo")
    @ApiOperation("Update info like name, sale type and time, non-price affecting")
    public Mono<OnSaleRequest> updateSaleInfo(@RequestBody OnSaleRequest request) {
        return promotionService.updateSaleInfo(request);
    }

    @PostMapping("/updatePrice")
    @ApiOperation("Update sale discount percent or fixed amount. price affecting")
    public Mono<OnSaleRequest> updateSalePrice(@RequestBody OnSaleRequest request) {
        return promotionService.updateSalePrice(request);
    }

    @PostMapping("/updateStatus")
    @ApiOperation("Update sale to be online or off line, price affecting")
    public Mono<OnSaleRequest> updateSaleStatus(@RequestBody OnSaleRequest request) {
        return promotionService.updateSaleStatus(request);
    }

    @DeleteMapping("/delete/{promotionSaleId}")
    @ApiOperation("")
    public Mono<Void> delete(@PathVariable int promotionSaleId) {
        return promotionService.delete(promotionSaleId);
    }
}
