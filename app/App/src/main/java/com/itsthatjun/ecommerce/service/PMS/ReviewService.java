package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewService {

    /**
     * Get detail of a review
     *
     * @param reviewId
     * @return
     */
    Mono<ProductReview> getDetailReview(int reviewId);

    /**
     * Get all reviews for a product
     *
     * @param skuCode
     * @return
     */
    Flux<ProductReview> getProductReviews(String skuCode);

    /**
     * Create a review for a product
     *
     * @param newReview
     * @return
     */
    Mono<ProductReview> createProductReview(ProductReview newReview);

    /**
     * Update a review
     *
     * @param newReview
     * @return
     */
    Mono<ProductReview> updateProductReviews(ProductReview newReview);

    /**
     * Delete a review
     *
     * @param skuCode
     * @return
     */
    Mono<Void> deleteProductReviews(String skuCode);
}
