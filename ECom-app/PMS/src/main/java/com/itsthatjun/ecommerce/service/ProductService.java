package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public interface ProductService {
    @ApiModelProperty(value = "")
    List<Product> listAllProduct();

    @ApiModelProperty(value = "")
    List<Product> listProduct(int pageNum, int pageSize);

    @ApiModelProperty(value = "")
    Product getProduct(int id);
}
