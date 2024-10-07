package com.itsthatjun.ecommerce.dao;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductPictures;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface AdminProductDao {

    @ApiModelProperty("get all product")
    List<Product> listAllProduct();

    @ApiModelProperty("")
    Product getProduct(@Param("productId") int productId);

    List<ProductSku> listAllSku(@Param("productId") int productId);

    @ApiModelProperty("get a pictures for product detail")
    List<ProductPictures> getProductPictures(@Param("productId") int productId);

    @ApiModelProperty("get a pictures for product detail")
    List<Map<String, String>> getProductAttributes(@Param("productId") int productId);

    @ApiModelProperty("get attribute name based on category name. e.g Smartphone attribute will be camera resolution")
    List<Map<String, Object>> getAttributeType(@Param("categoryName") String categoryName);
}
