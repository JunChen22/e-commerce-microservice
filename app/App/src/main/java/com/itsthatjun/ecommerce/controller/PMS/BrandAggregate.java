package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.model.BrandDTO;
import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
import com.itsthatjun.ecommerce.service.PMS.impl.BrandServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get all brands with page and size", description = "Get all brands with page and size")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all brands with page and size"),
            @ApiResponse(responseCode = "404", description = "No brands found")})
    @GetMapping("/listAll")
    public Flux<BrandDTO> listAllBrand() {
        return brandService.listAllBrand();
    }

    @Operation(summary = "Get all brands with page and size", description = "Get all brands with page and size")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all brands with page and size"),
            @ApiResponse(responseCode = "404", description = "No brands found")})
    @GetMapping("/list")
    public Flux<BrandDTO> listAllBrand(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "size", defaultValue = "3") int pageSize) {
        return brandService.listBrand(pageNum, pageSize);
    }

    @Operation(summary = "Get all product of this brand", description = "Get all product of this brand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all product of this brand"),
            @ApiResponse(responseCode = "404", description = "No product found")})
    @GetMapping("/product/{brandId}")
    public Flux<ProductDTO> getBrandProduct(@PathVariable int brandId) {
        return brandService.getBrandProduct(brandId);
    }

    @Operation(summary = "Get brand info", description = "Get brand info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get brand info"),
            @ApiResponse(responseCode = "404", description = "Brand not found")})
    @GetMapping("/{brandId}")
    public Mono<BrandDTO> getBrand(@PathVariable int brandId) {
        return brandService.getBrand(brandId);
    }
}
