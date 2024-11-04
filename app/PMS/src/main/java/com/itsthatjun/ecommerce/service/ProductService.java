package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    /**
     * list all products
     * @return list of products
     */
    Flux<ProductDTO> listAllProduct();

    /**
     * list products with pagination
     * @param pageNum page number
     * @param pageSize page size
     * @return list of products
     */
    Flux<ProductDTO> listProduct(int pageNum, int pageSize);

    /**
     * get product detail by id
     * @param id product id
     * @return product detail
     */
    Mono<ProductDetail> getProductDetail(int id);
}
