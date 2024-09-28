package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductReview;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewService {

    @ApiOperation(value = "get all reviews for a product")
    Flux<ProductReview> getProductReviews(int productId);

    @ApiOperation(value = "get all reviews made a user")
    Flux<ProductReview> getProductReviewsByUser(int useId);

    @ApiOperation(value = "create review for a product")
    Mono<ProductReview> createProductReview(ProductReview review);

    @ApiOperation(value = "update a review")
    Mono<ProductReview> updateProductReviews(ProductReview updatedReview);

    @ApiOperation(value = "Get product with page and size")
    Mono<Void> deleteProductReviews(int reviewId);
}
