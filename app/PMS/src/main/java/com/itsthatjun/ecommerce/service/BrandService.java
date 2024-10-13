package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.model.entity.Brand;
import com.itsthatjun.ecommerce.model.entity.Product;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BrandService {

    @ApiOperation("")
    Flux<Brand> listAllBrand(int pageNum, int pageSize);

    @ApiOperation("")
    Flux<Product> listAllBrandProduct(int brandId);

    @ApiOperation("")
    Mono<Brand> getBrand(int id);

    @ApiOperation("")
    Mono<Brand> adminCreateBrand(Brand brand, String operator);

    @ApiOperation("")
    Mono<Brand> adminUpdateBrand(Brand brand, String operator);

    @ApiOperation("")
    Mono<Void> adminDeleteBrand(int brandId, String operator);
}
