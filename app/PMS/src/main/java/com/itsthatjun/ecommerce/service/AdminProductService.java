package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.model.AdminProductDetail;
import com.itsthatjun.ecommerce.model.entity.Product;
import com.itsthatjun.ecommerce.model.entity.ProductSku;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminProductService {

    /**
     * List all products.
     * @return list of products
     */
    Flux<Product> listAllProduct();

    /**
     * List products with pagination.
     * @param pageNum page number
     * @param pageSize page size
     * @return list of products
     */
    Flux<Product> listProduct(int pageNum, int pageSize);

    /**
     * Get product detail by id.
     * @param id product id
     * @return product detail
     */
    Mono<AdminProductDetail> getProductDetail(int id);

    /**
     * Create a product with at least one sku variant.
     * @param productDetail product detail
     * @param operator operator
     * @return created product
     */
    Mono<Product> createProduct(AdminProductDetail productDetail, String operator);

    /**
     * Add a sku to existing product.
     * @param productDetail product detail
     * @param operator operator
     * @return updated product
     */
    Mono<Product> addProductSku(AdminProductDetail productDetail, String operator);

    /**
     * Update product info like category, name, description, subtitle and etc non-price affecting.
     * @param updatedProduct updated product
     * @param operator operator
     * @return updated product
     */
    Mono<Product> updateProductInfo(Product updatedProduct, String operator);

    /**
     * Update product publish status.
     * @param updatedProduct updated product
     * @param operator operator
     * @return updated product
     */
    Mono<Product> updateProductStatus(Product updatedProduct, String operator);

    /**
     * Update product sku publish status.
     * @param updateSku updated sku
     * @param operator operator
     * @return updated product
     */
    Mono<ProductSku> updateProductSkuStatus(ProductSku updateSku, String operator);

    /**
     * Update product stock of existing product. Adding stock to sku with newly added stock.
     * @param sku sku to update
     * @param addedStock added stock
     * @param operator operator
     */
    Mono<ProductSku> updateProductStock(ProductSku sku, int addedStock, String operator);

    /**
     * Update product price of existing product.
     * @param sku sku to update
     * @param operator operator
     * @return updated product
     */
    Mono<Product> updateProductPrice(ProductSku sku, String operator);

    /**
     * Remove sku from product. Product can have no sku, just holding information.
     * @param removeSku sku to remove
     * @param operator operator
     * @return removed sku
     */
    Mono<ProductSku> removeProductSku(ProductSku removeSku, String operator);

    /**
     * Soft delete. Delete just means status changed for archive, not actual delete from database
     * @param id product id
     * @param operator operator
     * @return void
     */
    Mono<Void> removeProduct(int id, String operator); // TODO: might add a method to undelete the product and moved from archive back to offline.
}
