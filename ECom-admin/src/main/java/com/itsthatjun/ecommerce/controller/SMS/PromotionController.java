package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.dto.sms.OnSaleRequest;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.service.SMS.impl.PromotionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/sale")
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
        String operatorName = getAdminName();
        request.setOperator(operatorName);
        return promotionService.createListSale(request, operatorName);
    }

    @PostMapping("/createBrandSale")
    @ApiOperation("")
    public Mono<OnSaleRequest> createBrandSale(@RequestBody OnSaleRequest request) {
        String operatorName = getAdminName();
        request.setOperator(operatorName);
        return promotionService.createBrandSale(request, operatorName);
    }

    @PostMapping("/createCategorySale")
    @ApiOperation("")
    public Mono<OnSaleRequest> createCategorySale(@RequestBody OnSaleRequest request) {
        String operatorName = getAdminName();
        request.setOperator(operatorName);
        return promotionService.createCategorySale(request, operatorName);
    }

    @PostMapping("/updateInfo")
    @ApiOperation("Update info like name, sale type and time, non-price affecting")
    public Mono<OnSaleRequest> updateSaleInfo(@RequestBody OnSaleRequest request) {
        String operatorName = getAdminName();
        return promotionService.updateSaleInfo(request, operatorName);
    }

    @PostMapping("/updatePrice")
    @ApiOperation("Update sale discount percent or fixed amount. price affecting")
    public Mono<OnSaleRequest> updateSalePrice(@RequestBody OnSaleRequest request) {
        String operatorName = getAdminName();
        return promotionService.updateSalePrice(request, operatorName);
    }

    @PostMapping("/updateStatus")
    @ApiOperation("Update sale to be online or off line, price affecting")
    public Mono<OnSaleRequest> updateSaleStatus(@RequestBody OnSaleRequest request) {
        String operatorName = getAdminName();
        return promotionService.updateSaleStatus(request, operatorName);
    }

    @DeleteMapping("/delete/{promotionSaleId}")
    @ApiOperation("")
    public Mono<Void> delete(@PathVariable int promotionSaleId) {
        String operatorName = getAdminName();
        return promotionService.delete(promotionSaleId, operatorName);
    }

    private String getAdminName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        String adminName = userDetail.getAdmin().getName();
        return adminName;
    }
}
