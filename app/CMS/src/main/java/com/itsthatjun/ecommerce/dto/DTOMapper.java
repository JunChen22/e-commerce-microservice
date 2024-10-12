package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.ImageDTO;
import com.itsthatjun.ecommerce.dto.model.QaDTO;
import com.itsthatjun.ecommerce.dto.model.VideoDTO;
import com.itsthatjun.ecommerce.model.entity.Article;
import com.itsthatjun.ecommerce.model.entity.ArticleImage;
import com.itsthatjun.ecommerce.model.entity.ArticleQa;
import com.itsthatjun.ecommerce.model.entity.ArticleVideo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DTOMapper {

    @Mapping(target = "QA", ignore = true)
    @Mapping(target = "images", ignore = true)
    @Mapping(target = "videos", ignore = true)
    ArticleInfo articleToArticleDTO(Article article);


    List<ImageDTO> imagesToImageDTOs(List<ArticleImage> articleImages);


    List<QaDTO> qasToQaDTOs(List<ArticleQa> articleQas);


    List<VideoDTO> videosToVideoDTOs(List<ArticleVideo> articleVideos);
}
