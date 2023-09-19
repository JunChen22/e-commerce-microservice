package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ProductService {

    @ApiOperation(value = "")
    Mono<Product> getProduct(int id);

    @ApiOperation(value = "")
    Flux<Product> listAllProduct();

    @ApiOperation(value = "")
    Flux<Product> listProduct(int pageNum, int pageSize);

    @ApiOperation(value = "")
    boolean createProduct(Product product);

    @ApiOperation(value = "")
    boolean updateProduct(Product product);

    @ApiOperation(value = "")
    boolean deleteProduct(int id);
}
