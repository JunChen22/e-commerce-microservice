package com.itsthatjun.ecommerce.dao;

import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductDao {

    @ApiModelProperty("get one")
    Product getProduct(@Param("id") int id);

    @ApiModelProperty("get a list")
    List<Product> getAllProduct();

    @ApiModelProperty("one with a list")
    ProductDetail getProductWithAtrribute(@Param("id") int id);

    @ApiModelProperty("list with a list")
    List<ProductDetail> getProductListWithAtrribute();

    @ApiModelProperty("update one")
    void updateProduct(@Param("id") int id, @Param("stock") int stock);

    @ApiModelProperty("update all")
    void updateProductList(@Param("productList") List<Product> productDetailList);

    @ApiModelProperty("add/insert one")
    void insertProduct(@Param("product") Product product);

    @ApiModelProperty("add a list")
    void insertProductList(@Param("productList") List<Product> productList);

    @ApiModelProperty("delete one")
    void deleteProduct(@Param("id") int id);

    @ApiModelProperty("delete all")
    void deleteProductList(@Param("productList") List<Product> productList);
}