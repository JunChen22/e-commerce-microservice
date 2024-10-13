package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.model.entity.Review;
import com.itsthatjun.ecommerce.model.entity.ReviewPictures;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ReviewService {

    @ApiOperation("")
    Mono<ProductReview> getDetailReview(int reviewId);

    @ApiOperation("")
    Flux<ProductReview> listProductAllReview(int productId);

    @ApiOperation("")
    Mono<Review> createReview(Review newReview, List<ReviewPictures> picturesList, int userId);

    @ApiOperation("")
    Mono<Review> updateReview(Review updatedReview, List<ReviewPictures> picturesList, int userId);

    @ApiOperation("")
    Mono<Void> deleteReview(int reviewId, int userId);

    @ApiOperation("Admin retrieves all reviews made one user")
    Flux<ProductReview> listAllReviewByUser(int userId);

    @ApiOperation("")
    Mono<Review> adminCreateReview(Review newReview, List<ReviewPictures> picturesList, String operator);

    @ApiOperation("")
    Mono<Review> adminUpdateReview(Review updatedReview, List<ReviewPictures> picturesList, String operator);

    @ApiOperation("")
    Mono<Void> adminDeleteReview(int reviewId, String operator);
}
