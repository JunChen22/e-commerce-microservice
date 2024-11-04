package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.Product;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Integer> {

    /**
     * Find product by slug and publish status.
     * Note: A custom write converter for R2DBC cannot convert a Java enum to a PostgreSQL enum directly, only primitive types.
     * Like java enum to varchar or integer but not java enum to postgres enum. Instead, the query casts the Java enum to a PostgreSQL enum using SQL.
     *
     * @param slug the unique slug of the product
     * @param publishStatus the publishing status to match
     * @return the matching product, if found
     */
    @Query("SELECT * FROM product WHERE slug = :slug AND publish_status = CAST(:publishStatus AS publish_status_enum)")
    Mono<Product> findBySlugAndPublishStatus(@Param("slug") String slug, @Param("publishStatus") String publishStatus);

    /**
     * Find all products that is published.
     * Note: A custom write converter for R2DBC cannot convert a Java enum to a PostgreSQL enum directly, only primitive types.
     * Like java enum to varchar or integer but not java enum to postgres enum. Instead, the query casts the Java enum to a PostgreSQL enum using SQL.
     *
     * @param publishStatus the publishing status to filter by
     * @return a flux of products with the specified publish status
     */
    @Query("SELECT * FROM product WHERE publish_status = CAST(:publishStatus AS publish_status_enum)")
    Flux<Product> findAllByPublishStatus(@Param("publishStatus") String publishStatus);
}
