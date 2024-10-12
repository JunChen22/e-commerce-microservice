package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.model.Brand;
import com.itsthatjun.ecommerce.model.Product;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BrandService {

    @ApiOperation(value = "List brands with page and size")
    Flux<Brand> listAllBrand(int pageNum, int pageSize);

    @ApiOperation(value = "Get all product of this brand")
    Flux<Product> getBrandProduct(int brandId);

    @ApiOperation(value = "Get brand info")
    Mono<Brand> getBrand(int brandId);
}
