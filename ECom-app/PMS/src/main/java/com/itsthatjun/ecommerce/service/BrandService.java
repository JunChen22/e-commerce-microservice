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
    boolean createBrand(Brand brand);

    @ApiOperation(value = "")
    boolean updateBrand(Brand brand);

    @ApiOperation(value = "")
    boolean deleteBrand(int id);
}
