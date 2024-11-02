package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.enums.status.PublishStatus;
import com.itsthatjun.ecommerce.model.entity.Article;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ArticleRepository extends ReactiveCrudRepository<Article, Integer> {

    /**
     * Find article by slug and publish status.
     * Note: A custom write converter for R2DBC cannot convert a Java enum to a PostgreSQL enum directly, only primitive types.
     * Like java enum to varchar or integer but not java enum to postgres enum. Instead, the query casts the Java enum to a PostgreSQL enum using SQL.
     *
     * @param slug the unique slug of the article
     * @param publishStatus the publishing status to match
     * @return the matching article, if found
     */
    @Query("SELECT * FROM article WHERE slug = :slug AND publish_status = CAST(:publishStatus AS publish_status_enum)")
    Mono<Article> findBySlugAndPublishStatus(@Param("slug") String slug, @Param("publishStatus") String publishStatus);

    /**
     * Find all articles that is published.
     * Note: A custom write converter for R2DBC cannot convert a Java enum to a PostgreSQL enum directly, only primitive types.
     * Like java enum to varchar or integer but not java enum to postgres enum. Instead, the query casts the Java enum to a PostgreSQL enum using SQL.
     *
     * @param publishStatus the publishing status to filter by
     * @return a flux of articles with the specified publish status
     */
    @Query("SELECT * FROM article WHERE publish_status = CAST(:publishStatus AS publish_status_enum)")
    Flux<Article> findAllByPublishStatus(@Param("publishStatus") String publishStatus);
}
