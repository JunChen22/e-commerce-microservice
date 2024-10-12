package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.model.AdminArticleInfo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AdminArticleService {

    /**
     * list all articles
     * @return list of articles
     */
    Flux<AdminArticleInfo> listAllArticles();

    /**
     * get article based on id, added delay and fault percentage for circuit breaker testing
     * @param articleId article id
     * @param delay delay in ms
     * @param faultPercent fault percentage
     * @return article info
     */
    Mono<AdminArticleInfo> getArticle(int articleId, int delay, int faultPercent);
//
//    /**
//     * create article and its media content
//     * @param article article info
//     * @param operator operator
//     * @return article info
//     */
//    Mono<AdminArticleInfo> createArticle(AdminArticleInfo article, String operator);
//
//    /**
//     * update/add article and its media content
//     * @param article article info
//     * @param operator operator
//     * @return article info
//     */
//    Mono<AdminArticleInfo> updateArticle(AdminArticleInfo article, String operator);
//
//    /**
//     * delete article and all of its media content
//     * @param articleId article id
//     * @param operator operator
//     * @return void
//     */
//    Mono<Void> deleteArticle(int articleId, String operator);
}
