package com.itsthatjun.ecommerce.service.CMS;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ArticleService {

    /**
     * List all articles
     * @return
     */
    Flux<ArticleInfo> listAllArticles();

    /**
     * List all articles with pagination
     * @return
     */
    Flux<ArticleInfo> listArticles(int page, int size);

    /**
     * Get article by slug
     * @param slug
     * @param delay
     * @param faultPercent
     * @return
     */
    Mono<ArticleInfo> getArticle(String slug, int delay, int faultPercent);
}
