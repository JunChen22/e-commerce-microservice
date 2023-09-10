package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface ProductService {

    @ApiOperation(value = "")
    List<Product> listAllProduct();

    @ApiOperation(value = "")
    List<Product> listProduct(int pageNum, int pageSize);

    @ApiOperation(value = "")
    Product getProduct(int id);

    @ApiOperation(value = "")
    boolean createProduct(Product product);

    @ApiOperation(value = "")
    boolean updateProduct(Product product);

    @ApiOperation(value = "")
    boolean deleteProduct(int id);
}
