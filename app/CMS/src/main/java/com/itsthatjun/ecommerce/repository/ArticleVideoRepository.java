package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.ArticleVideo;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ArticleVideoRepository extends ReactiveCrudRepository<ArticleVideo, Integer> {

    /**
     * Find all videos by article id
     * @param articleId
     * @return
     */
    Flux<ArticleVideo> findAllByArticleId(Integer articleId);
}
