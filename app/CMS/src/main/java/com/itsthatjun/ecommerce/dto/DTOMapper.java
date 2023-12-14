package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.admin.AdminArticleInfo;
import com.itsthatjun.ecommerce.dto.model.ArticleDTO;
import com.itsthatjun.ecommerce.dto.model.QaDTO;
import com.itsthatjun.ecommerce.mbg.model.Article;
import com.itsthatjun.ecommerce.mbg.model.ArticleImage;
import com.itsthatjun.ecommerce.mbg.model.ArticleQa;
import com.itsthatjun.ecommerce.mbg.model.ArticleVideo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DTOMapper {

    DTOMapper INSTANCE = Mappers.getMapper(DTOMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Article adminArticleInfoToArticle(AdminArticleInfo articleInfo);

    AdminArticleInfo articleToAdminArticleInfo(Article article, List<ArticleQa> QA, List<ArticleImage> images, List<ArticleVideo> videos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Article articleDTOToArticle(ArticleDTO articleDTO, Integer publishStatus);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "articleId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ArticleQa qaDTOToArticleQa(QaDTO qaDTO);

    List<ArticleQa> qaDTOToArticleQa(List<QaDTO> qaDTO);
}
