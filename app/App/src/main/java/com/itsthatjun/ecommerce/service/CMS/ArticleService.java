package com.itsthatjun.ecommerce.service.CMS;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArticleService {

    /**
     * List all articles
     * @return
     */
    @ApiOperation(value = "List all articles")
    Flux<ArticleInfo> listAllArticles();

    /**
     * Get article by slug
     * @param slug
     * @param delay
     * @param faultPercent
     * @return
     */
    Mono<ArticleInfo> getArticle(String slug, int delay, int faultPercent);
}
