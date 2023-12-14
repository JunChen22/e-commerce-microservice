package com.itsthatjun.ecommerce.service.CMS;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArticleService {

    @ApiOperation(value = "Get all articles")
    Flux<ArticleInfo> getAllArticle();

    @ApiOperation(value = "Get a article")
    Mono<ArticleInfo> getArticle(int articleId, int delay, int faultPercent);
}
