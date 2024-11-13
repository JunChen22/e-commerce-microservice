package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.ArticleImage;
import com.itsthatjun.ecommerce.model.entity.ArticleQa;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface ArticleImageRepository extends ReactiveCrudRepository<ArticleImage, Integer> {

    /**
     * Find all images by article id
     * @param articleId
     * @return
     */
    Flux<ArticleImage> findAllByArticleId(Integer articleId);

    /**
     * delete all image by article id
     * @param articleId
     * @return
     */
    @Query("DELETE FROM article_image WHERE article_id = :articleId")
    Mono<Void> deleteByArticleId(@Param("articleId") int articleId);
}
