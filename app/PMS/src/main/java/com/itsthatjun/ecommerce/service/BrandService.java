package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.model.BrandDTO;
import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BrandService {

    /**
     * list all brands
     * @return list all brands
     */
    Flux<BrandDTO> listAllBrand();

    /**
     * list all brands with pagination
     * @param pageNum page number
     * @param pageSize page size
     * @return list of brands
     */
    Flux<BrandDTO> listBrand(int pageNum, int pageSize);

    /**
     * list all products of a brand
     * @param slug brand slug
     * @return list of products
     */
    Flux<ProductDTO> listAllBrandProduct(String slug);

    /**
     * get brand by id
     * @param slug brand slug
     * @return brand
     */
    Mono<BrandDTO> getBrand(String slug);
}
