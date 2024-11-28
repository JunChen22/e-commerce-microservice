package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.ArticleDTO;
import com.itsthatjun.ecommerce.dto.model.ImageDTO;
import com.itsthatjun.ecommerce.dto.model.QaDTO;
import com.itsthatjun.ecommerce.dto.model.VideoDTO;
import com.itsthatjun.ecommerce.dto.admin.AdminArticleInfo;
import com.itsthatjun.ecommerce.model.entity.Article;
import com.itsthatjun.ecommerce.model.entity.ArticleImage;
import com.itsthatjun.ecommerce.model.entity.ArticleQa;
import com.itsthatjun.ecommerce.model.entity.ArticleVideo;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DTOMapper {

    // entity to dto

    ArticleDTO articleToArticleDTO(Article article);

    List<ImageDTO> imagesToImageDTOs(List<ArticleImage> articleImages);

    List<QaDTO> qasToQaDTOs(List<ArticleQa> articleQas);

    List<VideoDTO> videosToVideoDTOs(List<ArticleVideo> articleVideos);


    // dto to entity


    // other mapping

    /**
     * AdminArticleInfo to ArticleDTO, used to update article in redis using message queue
     * @param adminArticleInfo
     * @return
     */
    ArticleInfo adminArticleInfoToArticleInfo (AdminArticleInfo adminArticleInfo);
}
