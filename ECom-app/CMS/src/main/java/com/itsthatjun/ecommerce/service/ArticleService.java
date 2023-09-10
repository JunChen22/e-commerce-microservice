package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.Articles;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface ArticleService {

    @ApiOperation(value = "get all articles")
    List<Articles> getAllArticles();

    @ApiOperation(value = "get article based on id")
    Articles getArticle(int articleId);
}
