package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.admin.AdminProductDetail;
import com.itsthatjun.ecommerce.model.entity.Product;
import com.itsthatjun.ecommerce.service.admin.AdminProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product/admin")
@Tag(name = "Product related", description = "Product management service controller")
public class AdminProductController {

    private final AdminProductServiceImpl productService;

    @Autowired
    public AdminProductController(AdminProductServiceImpl productService) {
        this.productService = productService;
    }

    @Operation(summary = "List all products", description = "List all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products"),
            @ApiResponse(responseCode = "404", description = "No product found")})
    @GetMapping("/listAll")
    public Flux<Product> listAllProduct() {
        return productService.listAllProduct();
    }

    @Operation(summary = "Get product with page and size", description = "Get product with page and size")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products"),
            @ApiResponse(responseCode = "404", description = "No product found")})
    @GetMapping("/list")
    public Flux<Product> listProduct(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                           @RequestParam(value = "size", defaultValue = "5") int pageSize) {
        return productService.listProduct(pageNum, pageSize);
    }

    @Operation(summary = "Get product by id", description = "Get product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Specific product"),
            @ApiResponse(responseCode = "404", description = "Product not found")})
    @GetMapping("/{id}")
    public Mono<AdminProductDetail> listProduct(@PathVariable int id) {
        return productService.getProductDetail(id);
    }
}
