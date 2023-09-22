package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@RestController
@Api(tags = "Brand controller", description = "Brand Controller")
@RequestMapping("/brand")
public class BrandAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(BrandAggregate.class);

    private final WebClient webClient;

    private final String PMS_SERVICE_URL = "http://pms";

    @Autowired
    public BrandAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }
    @GetMapping("/listAll")
    @ApiOperation(value = "Get all brands")
    public Flux<Brand> getAllBrand(){
        String url = PMS_SERVICE_URL + "/listAll/";
        LOG.debug("Will call the getAllBrand API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Brand.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get brands with page and size")
    public Flux<Brand> getAllBrand(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                   @RequestParam(value = "size", defaultValue = "3") int pageSize){
        String url = PMS_SERVICE_URL + "/list/" + "?pageNum=" + pageNum + "&pageSize=" + pageSize;
        LOG.debug("Will call the getAllBrand API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Brand.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/product/{brandId}")
    @ApiOperation(value = "Get all product of this brand")
    public Flux<Product> getBrandProduct(@PathVariable int brandId){
        String url = PMS_SERVICE_URL + "/product/" + brandId;
        LOG.debug("Will call the getBrandProduct API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/{brandId}")
    @ApiOperation(value = "Get brand info")
    public Mono<Brand> getBrand(@PathVariable int brandId){
        String url = PMS_SERVICE_URL + "/" + brandId;
        LOG.debug("Will call the getBrand API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(Brand.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    public Mono<Health> getPmsHealth() {
        return getHealth(PMS_SERVICE_URL);
    }

    private Mono<Health> getHealth(String url) {
        url += "/actuator/health";
        LOG.debug("Will call the Health API on URL: {}", url);
        return webClient.get().uri(url).retrieve().bodyToMono(String.class)
                .map(s -> new Health.Builder().up().build())
                .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
                .log(LOG.getName(), FINE);
    }
}
