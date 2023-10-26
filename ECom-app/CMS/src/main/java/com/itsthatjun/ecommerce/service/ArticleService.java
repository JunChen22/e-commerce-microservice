package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ArticleInfo;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ArticleService {

    @ApiOperation(value = "get all articles")
    Flux<ArticleInfo> getAllArticles();

    @ApiOperation(value = "get article based on id")
    Mono<ArticleInfo> getArticle(int articleId, int delay, int faultPercent);

    @ApiOperation(value = "create article")
    Mono<ArticleInfo> createArticle(ArticleInfo article, String operator);

    @ApiOperation(value = "update/add article and its media content")
    Mono<ArticleInfo> updateArticle(ArticleInfo article, String operator);

    @ApiOperation(value = "delete article and all of its media content")
    Mono<Void> deleteArticle(int articleId, String operator);
}
