package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {

    @ApiOperation(value = "")
    Mono<ProductDetail> getProduct(int id);

    @ApiOperation(value = "")
    Flux<Product> listAllProduct();

    @ApiOperation(value = "")
    Flux<Product> listProduct(int pageNum, int pageSize);

    @ApiOperation(value = "create a product with at least one sku variant")
    Mono<Product> createProduct(Product product, List<ProductSku> skuList, String operator);

    @ApiOperation(value = "Add a sku to existing product.")
    Mono<Product> addProductSku(Product currentProduct, ProductSku newSKu, String operator);

    @ApiOperation(value = "Update product info like category, name, description, subtitle and etc non-price affecting.")
    Mono<Product> updateProductInfo(Product updatedProduct, String operator);

    @ApiOperation(value = "Update product publish status.")
    Mono<Product> updateProductStatus(Product updatedProduct, String operator);

    @ApiOperation(value = "Update product publish status.")
    Mono<ProductSku> updateProductSkuStatus(ProductSku updateSku, String operator);

    @ApiOperation(value = "Adding stock to sku with newly added stock.")
    Mono<ProductSku> updateProductStock(ProductSku sku, int addedStock, String operator);

    @ApiOperation(value = "Update product and its sku prices of existing product.")
    Mono<Product> updateProductPrice(List<ProductSku> productSkuList, String operator);

    @ApiOperation(value = "Remove/actual delete a sku from product. Product can have no sku, just holding information.")
    Mono<ProductSku> removeProductSku(ProductSku removeSku, String operator);

    @ApiOperation(value = "Delete just means status changed for archive, not actual delete from database")
    Mono<Void> deleteProduct(int id, String operator); // TODO: might add a method to undelete the product and moved from archive back to offline.
}
