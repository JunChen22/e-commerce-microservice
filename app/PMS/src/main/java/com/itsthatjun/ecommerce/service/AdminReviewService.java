package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.model.entity.Review;
import com.itsthatjun.ecommerce.model.entity.ReviewAlbumPicture;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface AdminReviewService {

    @ApiOperation("Admin retrieves all reviews made one user")
    Flux<ProductReview> listAllReviewByUser(int userId);

    @ApiOperation("")
    Mono<Review> adminCreateReview(Review newReview, List<ReviewAlbumPicture> picturesList, String operator);

    @ApiOperation("")
    Mono<Review> adminUpdateReview(Review updatedReview, List<ReviewAlbumPicture> picturesList, String operator);

    @ApiOperation("")
    Mono<Void> adminDeleteReview(int reviewId, String operator);
}
