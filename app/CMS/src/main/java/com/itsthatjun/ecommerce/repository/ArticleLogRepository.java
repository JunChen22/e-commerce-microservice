package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.ArticleChangeLog;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ArticleLogRepository extends ReactiveCrudRepository<ArticleChangeLog, Integer> {

    /**
     * Find all change logs by article id
     * @param articleId
     * @return
     */
    Flux<ArticleChangeLog> findAllByArticleId(Integer articleId);

    /**
     * Save a log.
     * @param articleChangeLog the log
     * @return the saved log
     */
    @Query("INSERT INTO article_change_log (article_id, update_action, description, operator) " +
            "VALUES (:#{#articleChangeLog.articleId}, CAST(:#{#articleChangeLog.updateAction} AS update_action_type_enum), " +
            ":#{#articleChangeLog.description}, :#{#articleChangeLog.operator}) RETURNING *")
    Mono<ArticleChangeLog> saveLog(ArticleChangeLog articleChangeLog);
}
