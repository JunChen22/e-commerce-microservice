package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.dto.pms.AdminProductDetail;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    @ApiOperation(value = "Get all product")
    Flux<Product> listAllProduct();

    @ApiOperation(value = "Get product with page and size")
    Flux<Product> listAllProduct(int pageNum, int pageSize);

    @ApiOperation(value = "Get product by id")
    Mono<AdminProductDetail> listProduct(int productId);

    @ApiOperation(value = "create a product with at least one sku variant")
    Mono<AdminProductDetail> createProduct(AdminProductDetail productDetail);

    @ApiOperation(value = "Add a sku to existing product.")
    Mono<AdminProductDetail> addProductSku(AdminProductDetail productDetail);

    @ApiOperation(value = "Update product info like category, name, description, subtitle and etc non-price affecting.")
    Mono<AdminProductDetail> updateProductInfo(AdminProductDetail productDetail);

    @ApiOperation(value = "Update product publish status.")
    Mono<AdminProductDetail> updateProductStatus(AdminProductDetail productDetail);

    @ApiOperation(value = "Update product publish status.")
    Mono<AdminProductDetail> updateProductSkuStatus(AdminProductDetail productDetail);

    @ApiOperation(value = "Adding stock to sku with newly added stock.")
    Mono<AdminProductDetail> updateProductStock(AdminProductDetail productDetail);

    @ApiOperation(value = "Update product and its sku prices of existing product.")
    Mono<AdminProductDetail> updateProductPrice(AdminProductDetail productDetail);

    @ApiOperation(value = "Remove/actual delete a sku from product. Product can have no sku, just holding information.")
    Mono<AdminProductDetail> removeProductSku(AdminProductDetail productDetail);

    @ApiOperation(value = "Delete just means status changed for archive, not actual delete from database")
    Mono<Void> deleteProduct(int productId);
}
