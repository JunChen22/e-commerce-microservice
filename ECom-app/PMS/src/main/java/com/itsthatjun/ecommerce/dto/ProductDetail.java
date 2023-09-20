package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.mbg.model.*;
import lombok.Data;

import java.util.List;

@Data
public class ProductDetail {

    private Product product;
    private List<Review> reviews;
    private List<ProductSku> skuVariants;

    private List<ProductPictures> picturesList;
    private List<String> productCategory;  // TODO: there's sub level for product category
    private List<ProductAttributeCategory> productAttributeCategoryList;
    private List<ProductAttribute> productAttributeList;
    private List<ProductAttributeType> productAttributeTypesList;
}
