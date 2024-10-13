package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    @ApiOperation("")
    Mono<ProductDetail> getProductDetail(int id);

    @ApiOperation("")
    Flux<ProductDTO> listAllProduct();

    @ApiOperation("")
    Flux<ProductDTO> listProduct(int pageNum, int pageSize);

}
