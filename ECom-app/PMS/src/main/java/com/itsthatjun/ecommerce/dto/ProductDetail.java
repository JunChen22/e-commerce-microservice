package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import com.itsthatjun.ecommerce.dto.model.ProductSkuDTO;
import com.itsthatjun.ecommerce.dto.model.ReviewDTO;
import com.itsthatjun.ecommerce.mbg.model.ProductAttribute;
import com.itsthatjun.ecommerce.mbg.model.ProductAttributeCategory;
import com.itsthatjun.ecommerce.mbg.model.ProductAttributeType;
import com.itsthatjun.ecommerce.mbg.model.ProductPictures;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ProductDetail extends ProductDTO implements Serializable {

    private List<ReviewDTO> reviews;
    private List<ProductSkuDTO> skuVariants;

    private List<ProductPictures> picturesList;
    private List<String> productCategory;  // TODO: there's sub level for product category
    private List<ProductAttributeCategory> productAttributeCategoryList;
    private List<ProductAttribute> productAttributeList;
    private List<ProductAttributeType> productAttributeTypesList;
}
