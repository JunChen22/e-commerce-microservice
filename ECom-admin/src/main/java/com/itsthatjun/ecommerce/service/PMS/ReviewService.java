package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.mbg.model.Review;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface ReviewService {

    @ApiOperation(value = "")
    List<Review> listProductAllReview(int productId);

    @ApiOperation(value = "")
    List<Review> listAllReviewByUser(int userId);

    @ApiOperation(value = "")
    int createReview( Review review);

    @ApiOperation(value = "")
    int updateReview(Review updatedReview);

    @ApiOperation(value = "")
    int deleteReview(int reviewId);
}
