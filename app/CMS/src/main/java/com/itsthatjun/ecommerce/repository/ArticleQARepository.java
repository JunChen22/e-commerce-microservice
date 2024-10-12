package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.ArticleQa;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ArticleQARepository extends ReactiveCrudRepository<ArticleQa, Integer> {

    /**
     * Find all QA by article id
     * @param articleId
     * @return
     */
    Flux<ArticleQa> findAllByArticleId(Integer articleId);
}
