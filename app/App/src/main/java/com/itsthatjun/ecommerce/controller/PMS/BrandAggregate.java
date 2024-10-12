package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.model.Brand;
import com.itsthatjun.ecommerce.model.Product;
import com.itsthatjun.ecommerce.service.PMS.impl.BrandServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "Brand controller", description = "Brand Controller")
@RequestMapping("/brand")
public class BrandAggregate {

    private final BrandServiceImpl brandService;

    @Autowired
    public BrandAggregate(BrandServiceImpl brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all brands with page and size")
    public Flux<Brand> listAllBrand(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                   @RequestParam(value = "size", defaultValue = "3") int pageSize) {
        Flux<Brand> brandFlux = brandService.listAllBrand(pageNum, pageSize);
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
