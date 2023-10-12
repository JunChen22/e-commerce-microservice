package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BrandService {

    @ApiOperation(value = "Get all brands")
    Flux<Brand> getAllBrand();

    @ApiOperation(value = "Get brands with page and size")
    Flux<Brand> getAllBrand(int pageNum, int pageSize);

    @ApiOperation(value = "Get all product of this brand")
    Flux<Product> getBrandProduct(int brandId);

    @ApiOperation(value = "Get brand info")
    Mono<Brand> getBrand(int brandId);

    @ApiOperation(value = "Create a brand")
    Mono<Brand> createBrand(Brand brand);

    @ApiOperation(value = "Update a brand")
    Mono<Brand> updateBrand(Brand brand);

    @ApiOperation(value = "Delete a brand")
    Mono<Void> deleteBrand(int brandId);
}
