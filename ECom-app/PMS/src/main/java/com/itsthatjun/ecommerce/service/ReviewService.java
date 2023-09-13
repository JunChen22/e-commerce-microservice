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
    int createReview(Review newReview, List<ReviewPictures> picturesList, int userId);

    @ApiOperation(value = "")
    int updateReview(Review updatedReview, List<ReviewPictures> picturesList, int userId);

    @ApiOperation(value = "")
    int deleteReview(int reviewId, int userId);

    @ApiOperation(value = "")
    List<ProductReview> listAllReviewByUser(int userId);

    @ApiOperation(value = "")
    void adminCreateReview(Review newReview, List<ReviewPictures> picturesList);

    @ApiOperation(value = "")
    void adminUpdateReview(Review updatedReview, List<ReviewPictures> picturesList);

    @ApiOperation(value = "")
    void adminDeleteReview(int reviewId);
}
