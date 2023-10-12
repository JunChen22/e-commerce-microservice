package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.PMS.impl.ProductServiceImpl;
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
@Api(tags = "Product controller", description = "product controller")
@RequestMapping("/product")
public class ProductAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ProductAggregate.class);

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
    @Cacheable(value = "productsCache", key = "'listAllProduct'")
    @ApiOperation(value = "Get all product")
    public List<Product> listAllProduct() {
        Flux<Product> productFlux = productService.listAllProduct();
        // Collect the elements of the Flux into a List
        List<Product> productList = productFlux.collectList().block();

        // Return the List<Product>
        return productList;
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get product with page and size")
    public Flux<Product> listAllProduct(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "size", defaultValue = "5") int pageSize) {
        return productService.listAllProduct(pageNum, pageSize);
    }
}
