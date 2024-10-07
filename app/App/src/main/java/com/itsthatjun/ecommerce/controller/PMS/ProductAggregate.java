package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductDetail;
import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
import com.itsthatjun.ecommerce.service.PMS.impl.ProductServiceImpl;
import io.swagger.annotations.ApiOperation;
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

    @GetMapping("/{productId}")
    @ApiOperation(value = "Get product by id")
    public Mono<ProductDetail> listProduct(@PathVariable int productId) {
        return productService.listProduct(productId);
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all product")
    public Flux<ProductDTO> listAllProduct() {
        return productService.listAllProduct();
    }

    /* TODO: Implement caching
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

    @GetMapping("/list")
    @ApiOperation(value = "Get product with page and size")
    public Flux<ProductDTO> listAllProduct(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "size", defaultValue = "5") int pageSize) {
        return productService.listAllProduct(pageNum, pageSize);
    }
}
