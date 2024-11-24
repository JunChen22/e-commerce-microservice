package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.model.entity.Brand;
import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import com.itsthatjun.ecommerce.service.admin.AdminBrandServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/brand/admin")
@Tag(name = "Brand related", description = "Brand management service controller")
public class AdminBrandController {

    private final AdminBrandServiceImpl brandService;

    @Autowired
    public AdminBrandController(AdminBrandServiceImpl brandService) {
        this.brandService = brandService;
    }

    @Operation(summary = "List all brands", description = "List all brands")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of brands returned successfully"),
            @ApiResponse(responseCode = "404", description = "No brand found")})
    @GetMapping("/listAll")
    public Flux<Brand> listAllBrand() {
        return brandService.listAllBrand();
    }

    @Operation(summary = "List all brands with page and size", description = "List all brands with page and size")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of brands returned successfully"),
            @ApiResponse(responseCode = "404", description = "No brand found")})
    @GetMapping("/listBrand")
    public Flux<Brand> listBrand(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                    @RequestParam(value = "size", defaultValue = "3") int pageSize) {
        return brandService.listBrand(pageNum, pageSize);
    }

    @Operation(summary = "Get all product of this brand", description = "Get all product of this brand")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products with this brand"),
            @ApiResponse(responseCode = "404", description = "No product found")})
    @GetMapping("/product/{slug}")
    public Flux<ProductDTO> getBrandProduct(@PathVariable String slug) {
        return brandService.listAllBrandProduct(slug);
    }

    @Operation(summary = "Get brand info", description = "Get brand info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Specific brand"),
            @ApiResponse(responseCode = "404", description = "Brand not found")})
    @GetMapping("/{slug}")
    public Mono<Brand> getBrand(@PathVariable String slug) {
        return brandService.getBrand(slug);
    }
}
