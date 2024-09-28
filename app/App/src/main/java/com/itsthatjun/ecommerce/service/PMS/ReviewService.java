package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.dto.ProductReview;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewService {

    @ApiOperation(value = "get detail of a review")
    Mono<ProductReview> getDetailReview(int reviewId);

    @ApiOperation(value = "get all reviews for a product")
    Flux<ProductReview> getProductReviews(int productId);

    @ApiOperation(value = "create review for a product")
    Mono<ProductReview> createProductReview(ProductReview newReview);

    @ApiOperation(value = "update a review")
    Mono<ProductReview> updateProductReviews(ProductReview newReview);

    @ApiOperation(value = "Get product with page and size")
    Mono<Void> deleteProductReviews(int reviewId);
}
