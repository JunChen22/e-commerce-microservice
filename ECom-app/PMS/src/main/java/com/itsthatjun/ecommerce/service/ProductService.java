package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {

    @ApiOperation("")
    Mono<ProductDetail> getProductDetail(int id);

    @ApiOperation("")
    Flux<ProductDTO> listAllProduct();

    @ApiOperation("")
    Flux<ProductDTO> listProduct(int pageNum, int pageSize);

}
