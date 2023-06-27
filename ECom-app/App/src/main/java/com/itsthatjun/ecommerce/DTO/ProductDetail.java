package com.itsthatjun.ecommerce.DTO;

import com.itsthatjun.ecommerce.mbg.model.Brand;
import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductAttribute;
import com.itsthatjun.ecommerce.mbg.model.Review;
import lombok.Data;

import java.util.List;

@Data
public class ProductDetail {

    private Product product;
    private Brand brand;
    private List<ProductAttribute> productAttributeList;
    private List<Review> reviews;

    /* TODO:
    private List<SKU> skuList
    private List<Picture> pictures
    private List<picture> descriptionPicures
    private List<ProductAttributeCategory> productAttributeCategoryList;
    private List<ProductAttributeType> productAttributeTypesList;
    private List<Coupon> couponList;

     */
}
