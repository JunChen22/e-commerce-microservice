package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.ArticleVideo;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ArticleVideoRepository extends ReactiveCrudRepository<ArticleVideo, Integer> {

    /**
     * Find all videos by article id
     * @param articleId
     * @return
     */
    Flux<ArticleVideo> findAllByArticleId(Integer articleId);

    /**
     * delete all article video by article id
     * @param articleId
     * @return
     */
    @Query("DELETE FROM article_video WHERE article_id = :articleId")
    Mono<Void> deleteByArticleId(@Param("articleId") int articleId);
}
