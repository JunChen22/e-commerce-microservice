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
public class AdminArticleInfo extends Article implements Serializable {

    private static final long serialVersionUID = 1L;

    public AdminArticleInfo(Article article) {
        this.setId(article.getId());
        this.setTitle(article.getTitle());
        this.setSlug(article.getSlug());
        this.setBody(article.getBody());
        this.setPublishStatus(article.getPublishStatus());
        this.setCreatedAt(article.getCreatedAt());
        this.setUpdatedAt(article.getUpdatedAt());
    }

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
