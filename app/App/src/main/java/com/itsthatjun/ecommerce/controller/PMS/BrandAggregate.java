package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.PMS.impl.BrandServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Api(tags = "Brand controller", description = "Brand Controller")
@RequestMapping("/brand")
public class BrandAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(BrandAggregate.class);

    private final BrandServiceImpl brandService;

    @Autowired
    public BrandAggregate(BrandServiceImpl brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all brands")
    public Flux<Brand> getAllBrand() {
        Flux<Brand> brandFlux = brandService.getAllBrand();
        return brandFlux;
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get brands with page and size")
    public Flux<Brand> getAllBrand(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                   @RequestParam(value = "size", defaultValue = "3") int pageSize) {
        Flux<Brand> brandFlux = brandService.getAllBrand(pageNum, pageSize);
        return brandFlux;
    }

    @GetMapping("/product/{brandId}")
    @ApiOperation(value = "Get all product of this brand")
    public Flux<Product> getBrandProduct(@PathVariable int brandId) {
        Flux<Product> productFlux = brandService.getBrandProduct(brandId);
        return productFlux;
    }

    @GetMapping("/{brandId}")
    @ApiOperation(value = "Get brand info")
    public Mono<Brand> getBrand(@PathVariable int brandId) {
        return brandService.getBrand(brandId);
    }
}
