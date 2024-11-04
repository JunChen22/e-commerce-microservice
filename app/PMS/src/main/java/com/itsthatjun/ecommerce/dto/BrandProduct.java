package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class BrandProduct implements Serializable {
    List<ProductDTO> products;
}
