package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import com.itsthatjun.ecommerce.service.impl.ProductServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

@RestController
@RequestMapping("/product")
@Api(tags = "Product related", description = "Product management service controller")
public class ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    private final ProductServiceImpl productService;

    private final Scheduler scheduler;

    @Autowired
    public ProductController(ProductServiceImpl productService,
                             @Qualifier("scheduler") Scheduler scheduler) {
        this.productService = productService;
        this.scheduler = scheduler;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all product")
    public Flux<Product> listAllProduct(){
        return productService.listAllProduct().subscribeOn(scheduler);
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get product with page and size")
    public Flux<Product> listAllProduct(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "size", defaultValue = "5") int pageSize){
        return productService.listProduct(pageNum, pageSize).subscribeOn(scheduler);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Get product by id")
    public Mono<ProductDetail> listProduct(@PathVariable int id){
        return productService.getProduct(id);
    }











    /*
    @PostMapping("/admin/create")
    @ApiOperation(value = "Create a product")
    public Product createProduct(@RequestBody Product product){
        productService.createProduct(product);
        return product;
    }

    @PostMapping("/admin/update")
    @ApiOperation(value = "Update a product")
    public Product updateProduct(@RequestBody Product product){
        productService.updateProduct(product);
        return product;
    }


    @DeleteMapping("/admin/delete/{id}")
    @ApiOperation(value = "Delete a product")
    public String deleteProduct(@PathVariable int id){
        productService.deleteProduct(id);
    }

     */
}
