package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductDetail;
import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductService {

    @ApiOperation(value = "Get product by id")
    Mono<ProductDetail> listProduct(int id);

    @ApiOperation(value = "Get all product")
    Flux<ProductDTO> listAllProduct();

    @ApiOperation(value = "Get product with page and size")
    Flux<ProductDTO> listAllProduct(int pageNum, int pageSize);
}
