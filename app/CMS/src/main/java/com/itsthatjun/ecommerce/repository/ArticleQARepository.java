package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.ArticleQa;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ArticleQARepository extends ReactiveCrudRepository<ArticleQa, Integer> {

    /**
     * Find all QA by article id
     * @param articleId
     * @return
     */
    Flux<ArticleQa> findAllByArticleId(Integer articleId);

    /**
     * delete all QA by article id
     * @param articleId
     * @return
     */
    @Query("DELETE FROM article_QA WHERE article_id = :articleId")
    Mono<Void> deleteByArticleId(@Param("articleId") int articleId);
}
