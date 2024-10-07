package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ArticleInfo;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArticleService {

    @ApiOperation(value = "list all articles")
    Flux<ArticleInfo> listAllArticles();

    @ApiOperation(value = "get article based on id")
    Mono<ArticleInfo> getArticle(int articleId, int delay, int faultPercent);
}
