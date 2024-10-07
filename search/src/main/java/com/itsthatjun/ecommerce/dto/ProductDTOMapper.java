package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.document.elasticsearch.EsProduct;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductDTOMapper {

    ProductDTOMapper INSTANCE = Mappers.getMapper(ProductDTOMapper.class);

    @Mapping(target = "id", ignore = true)
    EsProduct productDTOToEsProduct(ProductDTO productDTO);
}
