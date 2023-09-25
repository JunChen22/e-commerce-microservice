package com.itsthatjun.ecommerce.controller.PMS;

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
@Api(tags = "Product controller", description = "product controller")
@RequestMapping("/product")
public class ProductAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ProductAggregate.class);

    private final WebClient webClient;

    private final String PMS_SERVICE_URL = "http://pms/product";

    @Autowired
    public ProductAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all product")
    public Flux<Product> listAllProduct(){
        String url = PMS_SERVICE_URL + "/listAll";

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get product by id")
    public Mono<Product> listProduct(@PathVariable int id){
        String url = PMS_SERVICE_URL + "/" + id;
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
}
