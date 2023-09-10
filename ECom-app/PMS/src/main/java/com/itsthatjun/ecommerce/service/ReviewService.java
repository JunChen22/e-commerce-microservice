package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.mbg.model.Review;
import com.itsthatjun.ecommerce.mbg.model.ReviewPictures;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface ReviewService {

    @ApiOperation(value = "")
    ProductReview getDetailReview(int reviewId);

    @ApiOperation(value = "")
    List<ProductReview> listProductAllReview(int productId);

    @ApiOperation(value = "")
    int createReview(Review newReview, List<ReviewPictures> picturesList, int userId);

    @ApiOperation(value = "")
    int updateReview(Review updatedReview, List<ReviewPictures> picturesList, int userId);

    @ApiOperation(value = "")
    int deleteReview(int reviewId, int userId);
}
