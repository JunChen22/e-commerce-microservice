package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.model.BrandDTO;
import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
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
    public Flux<BrandDTO> listAllBrand() {
        return brandService.listAllBrand();
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get all brands with page and size")
    public Flux<BrandDTO> listAllBrand(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "size", defaultValue = "3") int pageSize) {
        return brandService.listBrand(pageNum, pageSize);
    }

    @GetMapping("/product/{brandId}")
    @ApiOperation(value = "Get all product of this brand")
    public Flux<ProductDTO> getBrandProduct(@PathVariable int brandId) {
        return brandService.getBrandProduct(brandId);
    }

    @GetMapping("/{brandId}")
    @ApiOperation(value = "Get brand info")
    public Mono<BrandDTO> getBrand(@PathVariable int brandId) {
        return brandService.getBrand(brandId);
    }
}
