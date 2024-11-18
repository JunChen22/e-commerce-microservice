package com.itsthatjun.ecommerce.dto.pms;

import com.itsthatjun.ecommerce.dto.pms.model.ProductDTO;
import com.itsthatjun.ecommerce.dto.pms.model.ProductPictureDTO;
import com.itsthatjun.ecommerce.dto.pms.model.ProductSkuDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ProductDetail implements Serializable {

    private ProductDTO product;

    private List<ProductSkuDTO> skuVariants;

    private List<ProductPictureDTO> picturesList;

    private Map<String, String> attributes;
}
