package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.document.elasticsearch.EsProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductDTOMapper {

    @Mapping(target = "id", ignore = true)
    EsProduct productDTOToEsProduct(ProductDTO productDTO);
}
