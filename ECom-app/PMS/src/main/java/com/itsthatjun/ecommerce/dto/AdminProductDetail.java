package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductPictures;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AdminProductDetail {

    private Product product;

    private List<ProductSku> skuVariants;

    private List<ProductPictures> picturesList;

    private Map<String, String> attributes;

    private Integer stock;
}
