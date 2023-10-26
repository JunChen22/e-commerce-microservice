package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.mbg.model.Review;
import com.itsthatjun.ecommerce.mbg.model.ReviewPictures;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReviewService {

    @ApiOperation(value = "")
    Mono<ProductReview> getDetailReview(int reviewId);

    @ApiOperation(value = "")
    Flux<ProductReview> listProductAllReview(int productId);

    @ApiOperation(value = "")
    Mono<Review> createReview(Review newReview, List<ReviewPictures> picturesList, int userId);

    @ApiOperation(value = "")
    Mono<Review> updateReview(Review updatedReview, List<ReviewPictures> picturesList, int userId);

    @ApiOperation(value = "")
    Mono<Void> deleteReview(int reviewId, int userId);

    @ApiOperation(value = "Admin retrieves all reviews made one user")
    Flux<ProductReview> listAllReviewByUser(int userId);

    @ApiOperation(value = "")
    Mono<Review> adminCreateReview(Review newReview, List<ReviewPictures> picturesList, String operator);

    @ApiOperation(value = "")
    Mono<Review> adminUpdateReview(Review updatedReview, List<ReviewPictures> picturesList, String operator);

    @ApiOperation(value = "")
    Mono<Void> adminDeleteReview(int reviewId, String operator);
}
