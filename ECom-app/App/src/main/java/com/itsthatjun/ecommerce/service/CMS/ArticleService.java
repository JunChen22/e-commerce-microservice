package com.itsthatjun.ecommerce.service.CMS;

import com.itsthatjun.ecommerce.dto.Articles;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArticleService {

    @ApiOperation(value = "Get all articles")
    Flux<Articles> getAllArticle();

    @ApiOperation(value = "Get a article")
    Mono<Articles> getArticle(int articleId, int delay, int faultPercent);
}
