package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.Article;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ArticleRepository extends ReactiveCrudRepository<Article, Integer> {

    /**
     * Find article by slug and publish status
     * @param slug
     * @param publishStatus
     * @return
     */
    Mono<Article> findBySlugAndPublishStatus(String slug, int publishStatus);

    /**
     * Find all articles that os published
     * @return
     */
    Flux<Article> findAllByPublishStatus(int publishStatus);
}
