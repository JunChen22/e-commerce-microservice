package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.dto.model.ArticleDTO;
import com.itsthatjun.ecommerce.dto.model.ImageDTO;
import com.itsthatjun.ecommerce.dto.model.QaDTO;
import com.itsthatjun.ecommerce.dto.model.VideoDTO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ArticleInfo implements Serializable {

    private ArticleDTO article;

    /**
     * question and answer
     */
    private List<QaDTO> QA;

    /**
     * images
     */
    private List<ImageDTO> images;

    /**
     * videos
     */
    private List<VideoDTO> videos;
}
