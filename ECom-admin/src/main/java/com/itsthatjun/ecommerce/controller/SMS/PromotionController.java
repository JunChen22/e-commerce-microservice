package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.dto.sms.OnSaleRequest;
import com.itsthatjun.ecommerce.dto.sms.event.SmsAdminSaleEvent;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import com.itsthatjun.ecommerce.service.SMS.impl.PromotionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;


import javax.servlet.http.HttpSession;

import static com.itsthatjun.ecommerce.dto.sms.event.SmsAdminSaleEvent.Type.*;
import static java.util.logging.Level.FINE;

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

    @GetMapping("/AllSale")
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

    @PostMapping("/create")
    @ApiOperation("")
    public Mono<OnSaleRequest> createSale(@RequestBody OnSaleRequest request, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        return promotionService.createSale(request, operatorName);
    }

    @PostMapping("/updateInfo")
    @ApiOperation("")
    public Mono<OnSaleRequest> updateSaleInfo(@RequestBody OnSaleRequest request, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        return promotionService.updateSaleInfo(request, operatorName);
    }

    @PostMapping("/updatePrice")
    @ApiOperation("")
    public Mono<OnSaleRequest> updateSalePrice(@RequestBody OnSaleRequest request, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        return promotionService.updateSalePrice(request, operatorName);
    }

    @PostMapping("/updateStatus")
    @ApiOperation("")
    public Mono<OnSaleRequest> updateSaleStatus(@RequestBody OnSaleRequest request, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        return promotionService.updateSaleStatus(request, operatorName);
    }

    @DeleteMapping("/delete/{promotionSaleId}")
    @ApiOperation("")
    public Mono<Void> delete(@PathVariable int promotionSaleId, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        return promotionService.delete(promotionSaleId, operatorName);
    }
}
