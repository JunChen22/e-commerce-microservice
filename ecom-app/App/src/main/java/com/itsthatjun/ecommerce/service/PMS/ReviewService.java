package com.itsthatjun.ecommerce.service.PMS;

import com.itsthatjun.ecommerce.dto.ProductReview;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReviewService {

    @ApiOperation(value = "get detail of a review")
    public Mono<ProductReview> getDetailReview(int reviewId);

    @ApiOperation(value = "get all reviews for a product")
    public Flux<ProductReview> getProductReviews(int productId);

    @ApiOperation(value = "create review for a product")
    public Mono<ProductReview> createProductReview(ProductReview newReview, int userId);

    @ApiOperation(value = "update a review")
    public Mono<ProductReview> updateProductReviews(ProductReview newReview, int userId);

    @ApiOperation(value = "Get product with page and size")
    public Mono<Void> deleteProductReviews(int reviewId, int userId);
}
