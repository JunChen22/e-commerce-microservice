package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import com.itsthatjun.ecommerce.mbg.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    ProductDTO productToProductDTO(Product product);

    List<ProductDTO> productToProductDTO(List<Product> productList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brandId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "attributeCategoryId", ignore = true)
    @Mapping(target = "newStatus", ignore = true)
    @Mapping(target = "recommendStatus", ignore = true)
    @Mapping(target = "verifyStatus", ignore = true)
    @Mapping(target = "pictureAlbum", ignore = true)
    @Mapping(target = "onSaleStatus", ignore = true)
    @Mapping(target = "lowStock", ignore = true)
    @Mapping(target = "unitSold", ignore = true)
    @Mapping(target = "descriptionAlbumId", ignore = true)
    @Mapping(target = "deleteStatus", ignore = true)
    @Mapping(target = "publishStatus", ignore = true)
    @Mapping(target = "note", ignore = true)
    Product productDTOToProduct(ProductDTO productDTO);
}
