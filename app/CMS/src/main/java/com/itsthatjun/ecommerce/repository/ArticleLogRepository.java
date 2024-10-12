package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.ArticleChangeLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ArticleLogRepository extends ReactiveCrudRepository<ArticleChangeLog, Integer> {

    /**
     * Find all change logs by article id
     * @param articleId
     * @return
     */
    Flux<ArticleChangeLog> findAllByArticleId(Integer articleId);
}
