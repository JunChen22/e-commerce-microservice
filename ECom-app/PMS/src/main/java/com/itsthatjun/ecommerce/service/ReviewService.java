package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.mbg.model.Review;
import com.itsthatjun.ecommerce.mbg.model.ReviewPictures;

import java.util.List;

public interface ReviewService {

    ProductReview getDetailReview(int reviewId);
    List<ProductReview> listProductAllReview(int productId);

    int createReview(Review newReview, List<ReviewPictures> picturesList, int userId);

    int updateReview(Review updatedReview, List<ReviewPictures> picturesList, int userId);

    int deleteReview(int reviewId, int userId);
}
