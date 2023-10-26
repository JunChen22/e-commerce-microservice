package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BrandService {

    @ApiOperation(value = "")
    Flux<Brand> listAllBrand();

    @ApiOperation(value = "")
    Flux<Brand> listBrand(int pageNum, int pageSize);

    @ApiOperation(value = "")
    Flux<Product> listAllBrandProduct(int brandId);

    @ApiOperation(value = "")
    Mono<Brand> getBrand(int id);

    @ApiOperation(value = "")
    Mono<Brand> adminCreateBrand(Brand brand, String operator);

    @ApiOperation(value = "")
    Mono<Brand> adminUpdateBrand(Brand brand, String operator);

    @ApiOperation(value = "")
    Mono<Void> adminDeleteBrand(int brandId, String operator);
}
