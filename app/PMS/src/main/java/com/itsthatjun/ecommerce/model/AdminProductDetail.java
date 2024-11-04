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

    private static final long serialVersionUID = 1L;

    public AdminProductDetail(Product product) {
        this.setId(product.getId());
        this.setBrandId(product.getBrandId());
        this.setBrandName(product.getBrandName());
        this.setName(product.getName());
        this.setSlug(product.getSlug());
        this.setCategoryId(product.getCategoryId());
        this.setCategoryName(product.getCategoryName());
        this.setAttributeCategoryId(product.getAttributeCategoryId());
        this.setSn(product.getSn());
        this.setConditionStatus(product.getConditionStatus());
        this.setRecommendStatus(product.getRecommendStatus());
        this.setVerifyStatus(product.getVerifyStatus());
        this.setSubTitle(product.getSubTitle());
        this.setCoverPicture(product.getCoverPicture());
        this.setPictureAlbum(product.getPictureAlbum());
        this.setDescription(product.getDescription());
        this.setOriginalPrice(product.getOriginalPrice());
        this.setOnSaleStatus(product.getOnSaleStatus());
        this.setSalePrice(product.getSalePrice());
        this.setStock(product.getStock());
        this.setLowStock(product.getLowStock());
        this.setUnitSold(product.getUnitSold());
        this.setWeight(product.getWeight());
        this.setKeywords(product.getKeywords());
        this.setDetailTitle(product.getDetailTitle());
        this.setDetailDesc(product.getDetailDesc());
        this.setDescriptionAlbumId(product.getDescriptionAlbumId());
        this.setDeleteStatus(product.getDeleteStatus());
        this.setPublishStatus(product.getPublishStatus());
        this.setCreatedAt(product.getCreatedAt());
        this.setUpdatedAt(product.getUpdatedAt());
        this.setNote(product.getNote());
    }

    private ProductSku skuVariants;

    private List<ProductAlbumPicture> picturesList;

    private Map<String, Attribute> attributes;
}
