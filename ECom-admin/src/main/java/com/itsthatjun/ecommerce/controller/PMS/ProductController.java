package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductDetail;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.service.PMS.impl.ProductServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        String operatorName = getAdminName();
        return productService.createProduct(productDetail, operatorName);
    }

    @PostMapping("/addProductSku")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Add a sku to existing product.")
    public Mono<ProductDetail> addProductSku(@RequestBody ProductDetail productDetail) {
        String operatorName = getAdminName();
        return productService.addProductSku(productDetail, operatorName);
    }

    @PostMapping("/updateProductInfo")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product info like category, name, description, subtitle and etc non-price affecting.")
    public Mono<ProductDetail> updateProductInfo(@RequestBody ProductDetail productDetail) {
        String operatorName = getAdminName();
        return productService.updateProductInfo(productDetail, operatorName);
    }

    @PostMapping("/updateProductStatus")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product publish status.")
    public Mono<ProductDetail> updateProductStatus(@RequestBody ProductDetail productDetail) {
        String operatorName = getAdminName();
        return productService.updateProductStatus(productDetail, operatorName);
    }

    @PostMapping("/updateProductSkuStatus")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product publish status.")
    public Mono<ProductDetail> updateProductSkuStatus(@RequestBody ProductDetail productDetail) {
        String operatorName = getAdminName();
        return productService.updateProductSkuStatus(productDetail, operatorName);
    }

    @PostMapping("/updateProductStock")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Adding stock to sku with newly added stock.")
    public Mono<ProductDetail> updateProductStock(@RequestBody ProductDetail productDetail) {
        String operatorName = getAdminName();
        return productService.updateProductStock(productDetail, operatorName);
    }

    @PostMapping("/updateProductPrice")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Update product and its sku prices of existing product.")
    public Mono<ProductDetail> updateProductPrice(@RequestBody ProductDetail productDetail) {
        String operatorName = getAdminName();
        return productService.updateProductPrice(productDetail, operatorName);
    }

    @PostMapping("/removeProductSku")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Remove/actual delete a sku from product. Product can have no sku, just holding information.")
    public Mono<ProductDetail> removeProductSku(@RequestBody ProductDetail productDetail) {
        String operatorName = getAdminName();
        return productService.removeProductSku(productDetail, operatorName);
    }

    @DeleteMapping("/delete/{productId}")
    @PreAuthorize("hasRole('ROLE_admin-product')")
    @ApiOperation(value = "Delete just means status changed for archive, not actual delete from database")
    public Mono<Void> deleteProduct(@PathVariable int productId) {
        String operatorName = getAdminName();
        return productService.deleteProduct(productId, operatorName);
    }

    private String getAdminName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        String adminName = userDetail.getAdmin().getName();
        return adminName;
    }
}
