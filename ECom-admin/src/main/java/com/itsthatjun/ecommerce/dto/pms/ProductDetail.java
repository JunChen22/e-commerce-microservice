package com.itsthatjun.ecommerce.dto.pms;

import com.itsthatjun.ecommerce.mbg.model.*;
import lombok.Data;

import java.util.List;

@Data
public class ProductDetail {

    private Product product;
    private Brand brand;
    private List<ProductAttribute> productAttributeList;
    private List<Review> reviews;

    // TODO: add these category to ProductDao currently these are null;
    private List<String> skuVariants;
    private List<ProductPictures> picturesList;
    private List<String> productCategory;  // TODO: there's sub level for product category
    private List<ProductAttributeCategory> productAttributeCategoryList;
    private List<ProductAttributeType> productAttributeTypesList;
}
