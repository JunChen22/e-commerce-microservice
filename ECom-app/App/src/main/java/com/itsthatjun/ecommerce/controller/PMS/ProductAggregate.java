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

    @Value("${app.PMS-service.host}")
    String productServiceURL;
    @Value("${app.PMS-service.port}")
    int port;

    @Autowired
    public ProductAggregate(WebClient.Builder  webClient, @Qualifier("publishEventScheduler")Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all product")
    public Flux<Product> listAllProduct(){
        String url = "http://" + productServiceURL + ":" + port + "/product/listAll";

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get product by id")
    public Mono<Product> listProduct(@PathVariable int id){
        String url = "http://" + productServiceURL + ":" + port + "/product/" + id;
        return webClient.get().uri(url).retrieve().bodyToMono(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get product with page and size")
    public Flux<Product> listAllProduct(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "size", defaultValue = "5") int pageSize){
        String url = "http://" + productServiceURL + ":" + port + "/list?page=" + pageNum + "&size=" + pageSize;

        return webClient.get().uri(url).retrieve().bodyToFlux(Product.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }
}
