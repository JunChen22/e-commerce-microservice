package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.controller.OMS.OrderAggregate;
import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@RestController
@Api(tags = "", description = "")
@RequestMapping("/product")
public class ProductAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ProductAggregate.class);

    private final WebClient webClient;

    private final Scheduler publishEventScheduler;

    private final String PMS_SERVICE_URL = "http://pms";

    @Autowired
    public ProductAggregate(WebClient.Builder  webClient, @Qualifier("publishEventScheduler")Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all product")
    public Flux<Product> listAllProduct(){
        String url = PMS_SERVICE_URL + "/product/listAll";

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get product by id")
    public Mono<Product> listProduct(@PathVariable int id){
        String url = PMS_SERVICE_URL + "/product/" + id;
        return webClient.get().uri(url).retrieve().bodyToMono(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get product with page and size")
    public Flux<Product> listAllProduct(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "size", defaultValue = "5") int pageSize){
        String url = PMS_SERVICE_URL + "/list?page=" + pageNum + "&size=" + pageSize;

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
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
