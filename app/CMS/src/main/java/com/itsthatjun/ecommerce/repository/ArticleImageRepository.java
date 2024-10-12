package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.ArticleImage;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ArticleImageRepository extends ReactiveCrudRepository<ArticleImage, Integer> {

    /**
     * Find all images by article id
     * @param articleId
     * @return
     */
    Flux<ArticleImage> findAllByArticleId(Integer articleId);
}
