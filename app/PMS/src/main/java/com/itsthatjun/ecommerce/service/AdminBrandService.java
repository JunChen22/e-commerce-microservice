package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.model.entity.Brand;
import com.itsthatjun.ecommerce.model.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminBrandService {

    /**
     * list all brands
     * @return list all brands
     */
    Flux<Brand> listAllBrand();

    /**
     * list all brands with pagination
     * @param pageNum page number
     * @param pageSize page size
     * @return list of brands
     */
    Flux<Brand> listBrand(int pageNum, int pageSize);

    /**
     * list all products of a brand
     * @param brandId brand id
     * @return list of products
     */
    Flux<Product> listAllBrandProduct(int brandId);

    /**
     * get brand by id
     * @param id brand id
     * @return brand
     */
    Mono<Brand> getBrand(int id);

    /**
     * create a brand
     * @param brand brand
     * @param operator operator
     * @return brand
     */
    Mono<Brand> createBrand(Brand brand, String operator);

    /**
     * update a brand
     * @param brand brand
     * @param operator operator
     * @return brand
     */
    Mono<Brand> updateBrand(Brand brand, String operator);

    /**
     * delete a brand
     * @param brandId brand id
     * @param operator operator
     * @return void
     */
    Mono<Void> deleteBrand(int brandId, String operator);
}
