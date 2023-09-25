package com.itsthatjun.ecommerce.controller.SMS;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;

@RestController
@Api(tags = "Sale controller", description = "Sale controller")
@RequestMapping("/sale")
public class SaleAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(SaleAggregate.class);

    private final WebClient webClient;

    private final String SMS_SERVICE_URL = "http://sms/sale";

    @Autowired
    public SaleAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @GetMapping("/AllPromotionSale")
    @ApiOperation("All sales including promotional sale(regular discount) and flash sale(could clearance or limited time discount")
    public Flux<PromotionSale> getAllPromotionSale() {
        String url = SMS_SERVICE_URL + "s/AllPromotionSale";
        LOG.debug("Will call the getAllPromotionSale API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(PromotionSale.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/AllPromotionSaleItem")
    @ApiOperation("")
    public Flux<Product> getAllPromotionSaleItem() {
        String url = SMS_SERVICE_URL + "/sale/AllPromotionSaleItem";
        LOG.debug("Will call the getAllPromotionSaleItem API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/AllFlashSaleItem")
    @ApiOperation("")
    public Flux<Product> getAllFlashSaleItem() {
        String url = SMS_SERVICE_URL + "/sale/AllFlashSaleItem";
        LOG.debug("Will call the getAllFlashSaleItem API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }
}
