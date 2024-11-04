package com.itsthatjun.ecommerce.service.admin;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.model.entity.Review;
import com.itsthatjun.ecommerce.model.entity.ReviewAlbumPicture;
import com.itsthatjun.ecommerce.service.AdminReviewService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AdminReviewServiceImpl implements AdminReviewService {
    @Override
    public Flux<ProductReview> listAllReviewByUser(int userId) {
        return null;
    }

    @Override
    public Mono<Review> adminCreateReview(Review newReview, List<ReviewAlbumPicture> picturesList, String operator) {
        return null;
    }

    @Override
    public Mono<Review> adminUpdateReview(Review updatedReview, List<ReviewAlbumPicture> picturesList, String operator) {
        return null;
    }

    @Override
    public Mono<Void> adminDeleteReview(int reviewId, String operator) {
        return null;
    }
}
