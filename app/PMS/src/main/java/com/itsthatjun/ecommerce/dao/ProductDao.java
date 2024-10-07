package com.itsthatjun.ecommerce.dao;

import com.itsthatjun.ecommerce.dto.ProductDetail;
import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import com.itsthatjun.ecommerce.dto.model.ProductPictureDTO;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface ProductDao {

    @ApiModelProperty("list all product")
    List<ProductDTO> listAllProduct();

    @ApiModelProperty("get a product detail, sku/variants, attribute , category and etc")
    ProductDetail getProductDetail(@Param("productId") int productId);

    @ApiModelProperty("get a pictures for product detail")
    List<ProductPictureDTO> getProductPictures(@Param("productId") int productId);

    @ApiModelProperty("get a pictures for product detail")
    List<Map<String, String>> getProductAttributes(@Param("productId") int productId);

    @ApiModelProperty("get attribute name based on category name. e.g Smartphone attribute will be camera resolution")
    List<Map<String, Object>> getAttributeType(@Param("categoryName") String categoryName);
}