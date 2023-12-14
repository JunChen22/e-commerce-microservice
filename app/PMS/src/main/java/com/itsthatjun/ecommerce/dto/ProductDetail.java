package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import com.itsthatjun.ecommerce.dto.model.ProductPictureDTO;
import com.itsthatjun.ecommerce.dto.model.ProductSkuDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ProductDetail extends ProductDTO implements Serializable {

    private List<ProductSkuDTO> skuVariants;

    private List<ProductPictureDTO> picturesList;

    private Map<String, String> attributes;
}
