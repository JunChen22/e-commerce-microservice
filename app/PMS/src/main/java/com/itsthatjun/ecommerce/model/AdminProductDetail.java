package com.itsthatjun.ecommerce.model;

import com.itsthatjun.ecommerce.dto.model.Attribute;
import com.itsthatjun.ecommerce.model.entity.Product;
import com.itsthatjun.ecommerce.model.entity.ProductPictures;
import com.itsthatjun.ecommerce.model.entity.ProductSku;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AdminProductDetail extends Product implements Serializable {

    private static final long serialVersionUID = 1L;

    public AdminProductDetail(Product product) {
        this.setId(product.getId());
        this.setName(product.getName());
        this.setSlug(product.getSlug());
        this.setBody(product.getBody());
        this.setPrice(product.getPrice());
        this.setDiscountPrice(product.getDiscountPrice());
        this.setPublishStatus(product.getPublishStatus());
        this.setCreatedAt(product.getCreatedAt());
        this.setUpdatedAt(product.getUpdatedAt());

    }

    private ProductSku skuVariants;

    private List<ProductPictures> picturesList;

    private Map<String, Attribute> attributes;

    private Integer stock;

}
