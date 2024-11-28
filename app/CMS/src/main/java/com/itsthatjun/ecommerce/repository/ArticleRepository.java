package com.itsthatjun.ecommerce.repository;

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
     * Find all articles. check for existing articles with the same slug
     *
     * @return a flux of all articles
     */
    Flux<Article> findAllBySlug(String slug);

    /**
     * Find all articles that is published.
     *
     * @param publishStatus the publishing status to filter by
     * @return a flux of articles with the specified publish status
     */
    @Query("SELECT * FROM article WHERE publish_status = CAST(:publishStatus AS publish_status_enum)")
    Flux<Article> findAllByPublishStatus(@Param("publishStatus") String publishStatus);

    /**
     * Find all articles that is published with pagination.
     *
     * @param publishStatus the publishing status to filter by
     * @param limit the maximum number of articles to return
     * @param offset the number of articles to skip
     * @return a flux of articles with the specified publish status
     */
    @Query("SELECT * FROM article WHERE publish_status = CAST(:publishStatus AS publish_status_enum) LIMIT :limit OFFSET :offset")
    Flux<Article> findAllByPublishStatusWithPagination(@Param("publishStatus") String publishStatus,
                                                       @Param("limit") int limit,
                                                       @Param("offset") int offset);

    /**
     * Find article by slug and publish status.
     *
     * @param slug the unique slug of the article
     * @param publishStatus the publishing status to match
     * @return the matching article, if found
     */
    @Query("SELECT * FROM article WHERE slug = :slug AND publish_status = CAST(:publishStatus AS publish_status_enum)")
    Mono<Article> findBySlugAndPublishStatus(@Param("slug") String slug, @Param("publishStatus") String publishStatus);

    /**
     * Save an article to the database.
     *
     * @param article the article to save
     * @return the saved article
     */
    @Query("INSERT INTO article (title, slug, author_name, body, publish_status) " +
            "VALUES (:#{#article.title}, :#{#article.slug}, :#{#article.authorName}, :#{#article.body}, " +
            "CAST(:#{#article.publishStatus.value} AS publish_status_enum)) RETURNING *")
    Mono<Article> saveArticle(@Param("article") Article article);

    /**
     * Update an article in the database.
     *
     * @param article the article to update
     * @return the updated article
     */
    @Query("UPDATE article SET title = :#{#article.title}, slug = :#{#article.slug}, " +
            "author_name = :#{#article.authorName}, body = :#{#article.body}, " +
            "publish_status = CAST(:#{#article.publishStatus.value} AS publish_status_enum), " +
            "updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = :#{#article.id} RETURNING *")
    Mono<Article> updateArticle(@Param("article") Article article);

    @Query("UPDATE article " +
            "SET lifecycle_status = CAST(:lifecycleStatus AS lifecycle_status_enum), " +
            "    publish_status = CAST(:publishStatus AS publish_status_enum) " +
            "WHERE id = :id " +
            "RETURNING *")
    Mono<Article> updateLifecycleAndPublishStatus(@Param("id") Integer articleId,
                                                  @Param("lifecycleStatus") String lifecycleStatus,
                                                  @Param("publishStatus") String publishStatus);
    /**
     * Delete an article from the database.
     *
     * @param article the article to delete
     * @return a mono that completes when the article is deleted
     */
    @Query("DELETE FROM article WHERE id = :article.id")
    Mono<Void> deleteArticle(Article article);
}
