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

    @PostMapping("/create")
    @ApiOperation(value = "create a product with at least one sku variant")
    public Mono<Product>  createProduct(@RequestBody ProductDetail productDetail) {
        Product product = productDetail.getProduct();
        List<ProductSku> skuList = productDetail.getSkuVariants();
        return productService.createProduct(product, skuList);
    }

    @PostMapping("/addProductSku")
    @ApiOperation(value = "Add a sku to existing product.")
    public Mono<Product> addProductSku(@RequestBody ProductDetail productDetail) {
        Product product = productDetail.getProduct();
        List<ProductSku> skuList = productDetail.getSkuVariants();
        return productService.addProductSku(product, skuList.get(0));
    }

    @PostMapping("/updateProductInfo")
    @ApiOperation(value = "Update product info like category, name, description, subtitle and etc non-price affecting.")
    public Mono<Product> updateProductInfo(@RequestBody ProductDetail productDetail) {
        Product product = productDetail.getProduct();
        return productService.updateProductInfo(product);
    }

    @PostMapping("/updateProductStatus")
    @ApiOperation(value = "Update product publish status.")
    public Mono<Product> updateProductStatus(@RequestBody ProductDetail productDetail) {
        Product product = productDetail.getProduct();
        return productService.updateProductStatus(product);
    }

    @PostMapping("/updateProductSkuStatus")
    @ApiOperation(value = "Update product publish status.")
    public Mono<ProductSku> updateProductSkuStatus(@RequestBody ProductDetail productDetail) {
        List<ProductSku> skuList = productDetail.getSkuVariants();
        return productService.updateProductSkuStatus(skuList.get(0));
    }

    @PostMapping("/updateProductStock/")
    @ApiOperation(value = "Adding stock to sku with newly added stock.")
    public Mono<ProductSku> updateProductStock(@RequestBody ProductDetail productDetail, @RequestParam int stock) {
        List<ProductSku> skuList = productDetail.getSkuVariants();
        return productService.updateProductStock(skuList.get(0), stock);
    }

    @PostMapping("/updateProductPrice")
    @ApiOperation(value = "Update product and its sku prices of existing product.")
    public Mono<Product> updateProductPrice(@RequestBody ProductDetail productDetail) {
        List<ProductSku> skuList = productDetail.getSkuVariants();
        return productService.updateProductPrice(skuList);
    }

    @PostMapping("/removeProductSku")
    @ApiOperation(value = "Remove/actual delete a sku from product. Product can have no sku, just holding information.")
    public Mono<ProductSku> removeProductSku(@RequestBody ProductDetail productDetail) {
        List<ProductSku> skuList = productDetail.getSkuVariants();
        return productService.removeProductSku(skuList.get(0));
    }

    @DeleteMapping("/delete/{productId}")
    @ApiOperation(value = "Delete just means status changed for archive, not actual delete from database")
    public void deleteProduct(@PathVariable int productId) {
        productService.deleteProduct(productId);
    }
}
