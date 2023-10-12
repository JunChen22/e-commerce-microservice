package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    @ApiOperation(value = "Get product by id")
    Mono<ProductDetail> listProduct(int id);

    @ApiOperation(value = "Get all product")
    Flux<Product> listAllProduct();

    @ApiOperation(value = "Get product with page and size")
    Flux<Product> listAllProduct(int pageNum, int pageSize);
}
