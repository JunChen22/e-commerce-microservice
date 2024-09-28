package com.itsthatjun.ecommerce.service.CMS;

import com.itsthatjun.ecommerce.dto.cms.AdminArticleInfo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface ArticleService {

    @ApiOperation(value = "Get all the articles")
    Flux<AdminArticleInfo> getAllArticle();

    @ApiOperation(value = "Get a specific article by article id")
    Mono<AdminArticleInfo> getArticle(@PathVariable int articleId);

    @ApiOperation(value = "create an article(buyer's guide, comparison, and etc)")
    Mono<AdminArticleInfo> createArticle(@RequestBody AdminArticleInfo articleInfo);

    @ApiOperation(value = "update an article and it's content")
    Mono<AdminArticleInfo> updateArticle(@RequestBody AdminArticleInfo articleInfo);

    @ApiOperation(value = "delete article and it's related content(QA, videos, and images)")
    Mono<Void> deleteArticle(@PathVariable int articleId);
}
