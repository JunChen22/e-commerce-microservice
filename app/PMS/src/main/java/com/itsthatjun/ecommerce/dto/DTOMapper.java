package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.BrandDTO;
import com.itsthatjun.ecommerce.dto.model.ProductDTO;
import com.itsthatjun.ecommerce.dto.model.ReviewDTO;
import com.itsthatjun.ecommerce.model.entity.Brand;
import com.itsthatjun.ecommerce.model.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DTOMapper {

    ProductDTO productToProductDTO(Product product);

    List<ProductDTO> productToProductDTO(List<Product> productList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "brandId", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "attributeCategoryId", ignore = true)
    @Mapping(target = "conditionStatus", ignore = true)
    @Mapping(target = "recommendStatus", ignore = true)
    @Mapping(target = "verifyStatus", ignore = true)
    @Mapping(target = "pictureAlbum", ignore = true)
    @Mapping(target = "onSaleStatus", ignore = true)
    @Mapping(target = "lowStock", ignore = true)
    @Mapping(target = "unitSold", ignore = true)
    @Mapping(target = "descriptionAlbumId", ignore = true)
    @Mapping(target = "deleteStatus", ignore = true)
    @Mapping(target = "publishStatus", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "note", ignore = true)
    Product productDTOToProduct(ProductDTO productDTO);

    BrandDTO brandToBrandDTO(Brand brand);

    List<BrandDTO> brandToBrandDTO(List<BrandDTO> brandDTOList);

    ReviewDTO reviewToReviewDTO(ReviewDTO reviewDTO);

    List<ReviewDTO> reviewToReviewDTO(List<ReviewDTO> reviewDTOList);
}
