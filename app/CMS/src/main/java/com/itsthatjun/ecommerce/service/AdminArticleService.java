package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.admin.AdminArticleInfo;
import com.itsthatjun.ecommerce.model.entity.Article;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminArticleService {

    /**
     * list all articles
     * @return list of articles
     */
    Flux<AdminArticleInfo> listAllArticles();

    /**
     * list articles with pagination
     * @param pageNum page number
     * @param pageSize page size
     * @return list of articles
     */
    Flux<AdminArticleInfo> listArticles(int pageNum, int pageSize);

    /**
     * get article based on id, added delay and fault percentage for circuit breaker testing
     * @param articleId article id
     * @param delay delay in ms
     * @param faultPercent fault percentage
     * @return article info
     */
    Mono<AdminArticleInfo> getArticle(int articleId, int delay, int faultPercent);

    /**
     * create article and its media content
     * @param article article info
     * @param operator operator
     * @return article info
     */
    Mono<AdminArticleInfo> createArticle(AdminArticleInfo article, String operator);

    /**
     * update/add article and its media content
     * @param article article info
     * @param operator operator
     * @return article info
     */
    Mono<AdminArticleInfo> updateArticle(AdminArticleInfo article, String operator);

    /**
     * update article status, like published, pending, draft, and paused for publish status and banned, archived, and normal, for lifecycle status
     * @param updateArticle article info
     * @param operator operator
     * @return void
     */
    Mono<Void> updateStatus(Article updateArticle, String operator);

    /**
     * restore article from soft delete
     * @param articleId article id
     * @param operator operator
     * @return void
     */
    Mono<Void> restoreArticle(int articleId, String operator);

    /**
     * delete article and all of its media content
     * @param articleId article id
     * @param operator operator
     * @return void
     */
    Mono<Void> deleteArticle(int articleId, String operator);

    /**
     * permanent delete article and all of its media content
     * @param articleId article id
     * @param operator operator
     * @return void
     */
    Mono<Void> permanentDeleteArticle(int articleId, String operator);
}
