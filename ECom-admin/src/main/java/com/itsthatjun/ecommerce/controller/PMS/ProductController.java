package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductDetail;
import com.itsthatjun.ecommerce.dto.pms.event.PmsAdminProductEvent;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.PMS.impl.ProductServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.pms.event.PmsAdminProductEvent.Type.*;
import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@RestController
@RequestMapping("/product")
@Api(tags = "Product related", description = "product related")
public class ProductController {

    private static final Logger LOG = LoggerFactory.getLogger(ProductController.class);

    private final ProductServiceImpl productService;

    @Autowired
    public ProductController(ProductServiceImpl productService) {
        this.productService = productService;
    }

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all product")
    public Flux<Product> listAllProduct() {
        return productService.listAllProduct();
    }

    @GetMapping("/list")
    @ApiOperation(value = "Get product with page and size")
    public Flux<Product> listAllProduct(@RequestParam(value = "page", defaultValue = "1") int pageNum,
                                        @RequestParam(value = "size", defaultValue = "5") int pageSize) {
        return productService.listAllProduct(pageNum, pageSize);
    }

    @GetMapping("/{productId}")
    @ApiOperation(value = "Get product by id")
    public Mono<ProductDetail> listProduct(@PathVariable int productId) {
        return productService.listProduct(productId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "create a product with at least one sku variant")
    public Mono<ProductDetail> createProduct(@RequestBody ProductDetail productDetail) {
        return productService.createProduct(productDetail);
    }

    @PostMapping("/addProductSku")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Add a sku to existing product.")
    public Mono<ProductDetail> addProductSku(@RequestBody ProductDetail productDetail) {
        return productService.addProductSku(productDetail);
    }

    @PostMapping("/updateProductInfo")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product info like category, name, description, subtitle and etc non-price affecting.")
    public Mono<ProductDetail> updateProductInfo(@RequestBody ProductDetail productDetail) {
        return productService.updateProductInfo(productDetail);
    }

    @PostMapping("/updateProductStatus")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product publish status.")
    public Mono<ProductDetail> updateProductStatus(@RequestBody ProductDetail productDetail) {
        return productService.updateProductStatus(productDetail);
    }

    @PostMapping("/updateProductSkuStatus")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product publish status.")
    public Mono<ProductDetail> updateProductSkuStatus(@RequestBody ProductDetail productDetail) {
        return productService.updateProductSkuStatus(productDetail);
    }

    @PostMapping("/updateProductStock")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Adding stock to sku with newly added stock.")
    public Mono<ProductDetail> updateProductStock(@RequestBody ProductDetail productDetail) {
        return productService.updateProductStock(productDetail);
    }

    @PostMapping("/updateProductPrice")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product and its sku prices of existing product.")
    public Mono<ProductDetail> updateProductPrice(@RequestBody ProductDetail productDetail) {
        return productService.updateProductPrice(productDetail);
    }

    @PostMapping("/removeProductSku")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Remove/actual delete a sku from product. Product can have no sku, just holding information.")
    public Mono<ProductDetail> removeProductSku(@RequestBody ProductDetail productDetail) {
        return productService.removeProductSku(productDetail);
    }

    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Delete just means status changed for archive, not actual delete from database")
    public Mono<Void> deleteProduct(@PathVariable int productId) {
        return productService.deleteProduct(productId);
    }
}
