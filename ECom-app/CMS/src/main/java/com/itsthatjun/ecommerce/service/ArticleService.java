package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ArticleInfo;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface ArticleService {

    @ApiOperation(value = "get all articles")
    List<ArticleInfo> getAllArticles();

    @ApiOperation(value = "get article based on id")
    ArticleInfo getArticle(int articleId);

    @ApiOperation(value = "create article")
    ArticleInfo createArticle(ArticleInfo article);

    @ApiOperation(value = "update/add article and its media content")
    ArticleInfo updateArticle(ArticleInfo article);

    @ApiOperation(value = "delete article and all of its media content")
    void deleteArticle(int articleId);
}
