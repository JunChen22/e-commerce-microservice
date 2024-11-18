package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductDetail;
import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    /**
     * Get product by id
     *
     * @param id
     * @return
     */
    Mono<ProductDetail> listProduct(int id);

    /**
     * Get all product
     *
     * @return
     */
    Flux<ProductDTO> listAllProduct();

    /**
     * Get product with page and size
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    Flux<ProductDTO> listAllProduct(int pageNum, int pageSize);
}
