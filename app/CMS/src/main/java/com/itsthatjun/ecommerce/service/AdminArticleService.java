package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.admin.AdminArticleInfo;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminArticleService {

    @ApiOperation(value = "get all articles")
    Flux<AdminArticleInfo> listAllArticles();

    @ApiOperation(value = "get article based on id")
    Mono<AdminArticleInfo> getArticle(int articleId, int delay, int faultPercent);

    @ApiOperation(value = "create article")
    Mono<AdminArticleInfo> createArticle(AdminArticleInfo article, String operator);

    @ApiOperation(value = "update/add article and its media content")
    Mono<AdminArticleInfo> updateArticle(AdminArticleInfo article, String operator);

    @ApiOperation(value = "delete article and all of its media content")
    Mono<Void> deleteArticle(int articleId, String operator);
}
