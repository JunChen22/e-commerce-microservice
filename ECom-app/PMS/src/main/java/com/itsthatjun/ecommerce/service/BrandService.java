package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface BrandService {

    @ApiOperation(value = "")
    List<Brand> listAllBrand();

    @ApiOperation(value = "")
    List<Brand> listBrand(int pageNum, int pageSize);

    @ApiOperation(value = "")
    List<Product> listAllBrandProduct(int brandId);

    @ApiOperation(value = "")
    Brand getBrand(int id);
}
