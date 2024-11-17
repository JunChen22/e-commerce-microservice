package com.itsthatjun.ecommerce.model;

import com.itsthatjun.ecommerce.model.entity.Article;
import com.itsthatjun.ecommerce.model.entity.ArticleImage;
import com.itsthatjun.ecommerce.model.entity.ArticleQa;
import com.itsthatjun.ecommerce.model.entity.ArticleVideo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class AdminArticleInfo implements Serializable {

    private Article article;

    /**
     * question and answer
     */
    private List<ArticleQa> QA;

    /**
     * images
     */
    private List<ArticleImage> images;

    /**
     * videos
     */
    private List<ArticleVideo> videos;
}
