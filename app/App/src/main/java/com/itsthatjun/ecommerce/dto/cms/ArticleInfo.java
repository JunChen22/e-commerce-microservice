package com.itsthatjun.ecommerce.dto.cms;

import com.itsthatjun.ecommerce.dto.cms.model.ArticleDTO;
import com.itsthatjun.ecommerce.dto.cms.model.ImageDTO;
import com.itsthatjun.ecommerce.dto.cms.model.QaDTO;
import com.itsthatjun.ecommerce.dto.cms.model.VideoDTO;
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
