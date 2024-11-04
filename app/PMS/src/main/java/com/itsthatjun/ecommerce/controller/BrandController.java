package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.model.BrandDTO;
import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import com.itsthatjun.ecommerce.service.impl.BrandServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/brand")
@Tag(name = "brand related", description = "brand management")
public class BrandController {

    private final BrandServiceImpl brandService;

    @Autowired
    public BrandController(BrandServiceImpl brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "List all brands with page and size")
    public Flux<BrandDTO> listAllBrand() {
        return brandService.listAllBrand();
    }

    @GetMapping("/listBrand")
    @ApiOperation(value = "List all brands with page and size")
    public Flux<BrandDTO> listBrand(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "size", defaultValue = "3") int pageSize) {
        return brandService.listBrand(pageNum, pageSize);
    }

    @GetMapping("/product/{slug}")
    @ApiOperation(value = "Get all product of this brand")
    public Flux<ProductDTO> getBrandProduct(@PathVariable String slug) {
        return brandService.listAllBrandProduct(slug);
    }

    @GetMapping("/{slug}")
    @ApiOperation(value = "Get brand info")
    public Mono<BrandDTO> getBrand(@PathVariable String slug) {
        return brandService.getBrand(slug);
    }
}
