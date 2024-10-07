package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.AdminProductDetail;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.service.PMS.impl.ProductServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/product")
@PreAuthorize("hasRole('ROLE_admin_product')")
@Tag(name = "Product related", description = "product related")
public class ProductController {

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
    public Mono<AdminProductDetail> listProduct(@PathVariable int productId) {
        return productService.listProduct(productId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasPermission('product:create')")
    @ApiOperation(value = "create a product with at least one sku variant")
    public Mono<AdminProductDetail> createProduct(@RequestBody AdminProductDetail productDetail) {
        return productService.createProduct(productDetail);
    }

    @PostMapping("/addProductSku")
    @PreAuthorize("hasPermission('product:create')")
    @ApiOperation(value = "Add a sku to existing product.")
    public Mono<AdminProductDetail> addProductSku(@RequestBody AdminProductDetail productDetail) {
        return productService.addProductSku(productDetail);
    }

    @PostMapping("/updateProductInfo")
    @PreAuthorize("hasPermission('product:update')")
    @ApiOperation(value = "Update product info like category, name, description, subtitle and etc non-price affecting.")
    public Mono<AdminProductDetail> updateProductInfo(@RequestBody AdminProductDetail productDetail) {
        return productService.updateProductInfo(productDetail);
    }

    @PostMapping("/updateProductStatus")
    @PreAuthorize("hasPermission('product:update')")
    @ApiOperation(value = "Update product publish status.")
    public Mono<AdminProductDetail> updateProductStatus(@RequestBody AdminProductDetail productDetail) {
        return productService.updateProductStatus(productDetail);
    }

    @PostMapping("/updateProductSkuStatus")
    @PreAuthorize("hasPermission('product:update')")
    @ApiOperation(value = "Update product publish status.")
    public Mono<AdminProductDetail> updateProductSkuStatus(@RequestBody AdminProductDetail productDetail) {
        return productService.updateProductSkuStatus(productDetail);
    }

    @PostMapping("/updateProductStock")
    @PreAuthorize("hasPermission('product:update')")
    @ApiOperation(value = "Adding stock to sku with newly added stock.")
    public Mono<AdminProductDetail> updateProductStock(@RequestBody AdminProductDetail productDetail) {
        return productService.updateProductStock(productDetail);
    }

    @PostMapping("/updateProductPrice")
    @PreAuthorize("hasPermission('product:update')")
    @ApiOperation(value = "Update product and its sku prices of existing product.")
    public Mono<AdminProductDetail> updateProductPrice(@RequestBody AdminProductDetail productDetail) {
        return productService.updateProductPrice(productDetail);
    }

    @PostMapping("/removeProductSku")
    @PreAuthorize("hasPermission('product:delete')")
    @ApiOperation(value = "Remove/actual delete a sku from product. Product can have no sku, just holding information.")
    public Mono<AdminProductDetail> removeProductSku(@RequestBody AdminProductDetail productDetail) {
        return productService.removeProductSku(productDetail);
    }

    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasPermission('product:delete')")
    @ApiOperation(value = "Delete just means status changed for archive, not actual delete from database")
    public Mono<Void> deleteProduct(@PathVariable int productId) {
        return productService.deleteProduct(productId);
    }
}
