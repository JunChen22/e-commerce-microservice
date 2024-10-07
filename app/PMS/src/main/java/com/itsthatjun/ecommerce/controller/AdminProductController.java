package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.AdminProductDetail;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.admin.AdminProductServiceImpl;
import io.swagger.annotations.ApiOperation;
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

    @GetMapping("/listAll")
    @ApiOperation("Get all product")
    public Flux<Product> listAllProduct() {
        return productService.listAllProduct();
    }

    @GetMapping("/list")
    @ApiOperation("Get product with page and size")
    public Flux<Product> listAllProduct(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                           @RequestParam(value = "size", defaultValue = "5") int pageSize) {
        return productService.listProduct(pageNum, pageSize);
    }

    @GetMapping("/{id}")
    @ApiOperation("Get product by id")
    public Mono<AdminProductDetail> listProduct(@PathVariable int id) {
        return productService.getProductDetail(id);
    }
}
