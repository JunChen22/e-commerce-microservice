package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.dto.model.ReviewDTO;
import com.itsthatjun.ecommerce.model.entity.Review;
import com.itsthatjun.ecommerce.model.entity.ReviewAlbumPicture;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReviewService {

    /**
     * Lists all reviews for a specified product.
     * @param productId the ID of the product
     * @return a Flux stream of ProductReview objects
     */
    Flux<ProductReview> listProductAllReview(int productId);

    /**
     * Retrieves the details of a specific review.
     * @param reviewId the ID of the review
     * @return a Mono containing the detailed review information
     */
    Mono<ProductReview> getDetailReview(int reviewId);

    /**
     * Creates a new review for a product.
     * @param newReview the new Review object
     * @param picturesList list of ReviewPictures associated with the review
     * @param userId the ID of the user creating the review
     * @return a Mono of the created Review
     */
    Mono<ReviewDTO> createReview(Review newReview, List<ReviewAlbumPicture> picturesList, int userId);

    /**
     * Updates an existing review.
     * @param updatedReview the updated Review object
     * @param picturesList list of ReviewPictures associated with the review
     * @param userId the ID of the user updating the review
     * @return a Mono of the updated Review
     */
    Mono<ReviewDTO> updateReview(Review updatedReview, List<ReviewAlbumPicture> picturesList, int userId);

    /**
     * Deletes a review.
     * @param reviewId the ID of the review to be deleted
     * @param userId the ID of the user requesting the deletion
     * @return a Mono signaling completion
     */
    Mono<Void> deleteReview(int reviewId, int userId);
}
