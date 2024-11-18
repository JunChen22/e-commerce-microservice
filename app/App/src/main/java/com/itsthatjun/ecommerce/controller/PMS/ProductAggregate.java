package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductDetail;
import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
import com.itsthatjun.ecommerce.service.PMS.impl.ProductServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "Product controller", description = "product controller")
@RequestMapping("/product")
public class ProductAggregate {

    private final ProductServiceImpl productService;

    @Autowired
    public ProductAggregate(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @Operation(summary = "Get all product", description = "Get all product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all product"),
            @ApiResponse(responseCode = "404", description = "No product found")})
    @GetMapping("/listAll")
    public Flux<ProductDTO> listAllProduct() {
        return productService.listAllProduct();
    }

    /* TODO: Implement caching
    @Operation(summary = "Get all product", description = "Get all product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all product"),
            @ApiResponse(responseCode = "404", description = "No product found")})
    @GetMapping("/listAll")
    @ApiOperation(value = "Get all products")
    public Flux<ProductDTO> listAllProduct() {
        // Check if cached data is available
        if (productService.checkForCache()) {
            return productService.getCachedAllProduct();
        } else {
            // Otherwise, fetch from the source and cache the result
            return productService.listAllProduct()
                    .doOnNext(product -> productService.cacheProduct(product))
                    .onErrorResume(error -> {
                        // Handle any errors and potentially return fallback data
                        return Flux.empty();
                    });
        }
    }
     */

    @Operation(summary = "Get all product with page and size", description = "Get all product with page and size")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all product with page and size"),
            @ApiResponse(responseCode = "404", description = "No product found")})
    @GetMapping("/list")
    public Flux<ProductDTO> listAllProduct(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "size", defaultValue = "5") int pageSize) {
        return productService.listAllProduct(pageNum, pageSize);
    }

    @Operation(summary = "Get product by id", description = "Get product by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get product by id"),
            @ApiResponse(responseCode = "404", description = "Product not found")})
    @GetMapping("/{productId}")
    public Mono<ProductDetail> listProduct(@PathVariable int productId) {
        return productService.listProduct(productId);
    }
}
