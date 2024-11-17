package com.itsthatjun.ecommerce.model;

import com.itsthatjun.ecommerce.dto.model.Attribute;
import com.itsthatjun.ecommerce.model.entity.Product;
import com.itsthatjun.ecommerce.model.entity.ProductAlbumPicture;
import com.itsthatjun.ecommerce.model.entity.ProductSku;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class AdminProductDetail extends Product implements Serializable {

    private ProductSku skuVariants;

    private List<ProductAlbumPicture> picturesList;

    /**
     * key: sku code
     * value: attribute value and unit
     */
    private Map<String, Attribute> attributes;
}
