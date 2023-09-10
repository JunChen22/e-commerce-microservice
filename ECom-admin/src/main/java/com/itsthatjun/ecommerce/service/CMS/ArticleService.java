package com.itsthatjun.ecommerce.service.CMS;

import com.itsthatjun.ecommerce.dto.CMS.Articles;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface ArticleService {

    @ApiOperation(value = "get all articles")
    List<Articles> getAllArticles();

    @ApiOperation(value = "get article based on id")
    Articles getArticle(int articleId);
    @ApiOperation(value = "create article")
    Articles createArticle(Articles article);

    @ApiOperation(value = "update/add article and its media content")
    Articles updateArticle(Articles article);

    @ApiOperation(value = "delete article and all of its media content")
    void deleteArticle(int articleId);
}
