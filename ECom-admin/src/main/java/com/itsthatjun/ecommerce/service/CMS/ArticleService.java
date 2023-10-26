package com.itsthatjun.ecommerce.service.CMS;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ArticleService {

    @ApiOperation(value = "Get all the articles")
    Flux<ArticleInfo> getAllArticle();

    @ApiOperation(value = "Get a specific article by article id")
    Mono<ArticleInfo> getArticle(@PathVariable int articleId);

    @ApiOperation(value = "create an article(buyer's guide, comparison, and etc)")
    Mono<ArticleInfo> createArticle(@RequestBody ArticleInfo articleInfo, String operator);

    @ApiOperation(value = "update an article and it's content")
    Mono<ArticleInfo> updateArticle(@RequestBody ArticleInfo articleInfo, String operator);

    @ApiOperation(value = "delete article and it's related content(QA, videos, and images)")
    Mono<Void> deleteArticle(@PathVariable int articleId, String operator);
}
