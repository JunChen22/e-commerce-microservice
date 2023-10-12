package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductDetail;
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
    Mono<ProductDetail> listProduct(int productId);

    @ApiOperation(value = "create a product with at least one sku variant")
    Mono<ProductDetail> createProduct(ProductDetail productDetail);

    @ApiOperation(value = "Add a sku to existing product.")
    Mono<ProductDetail> addProductSku(ProductDetail productDetail);

    @ApiOperation(value = "Update product info like category, name, description, subtitle and etc non-price affecting.")
    Mono<ProductDetail> updateProductInfo(ProductDetail productDetail);

    @ApiOperation(value = "Update product publish status.")
    Mono<ProductDetail> updateProductStatus(ProductDetail productDetail);

    @ApiOperation(value = "Update product publish status.")
    Mono<ProductDetail> updateProductSkuStatus(ProductDetail productDetail);

    @ApiOperation(value = "Adding stock to sku with newly added stock.")
    Mono<ProductDetail> updateProductStock(ProductDetail productDetail);

    @ApiOperation(value = "Update product and its sku prices of existing product.")
    Mono<ProductDetail> updateProductPrice(ProductDetail productDetail);

    @ApiOperation(value = "Remove/actual delete a sku from product. Product can have no sku, just holding information.")
    Mono<ProductDetail> removeProductSku(ProductDetail productDetail);

    @ApiOperation(value = "Delete just means status changed for archive, not actual delete from database")
    Mono<Void> deleteProduct(int productId);
}
