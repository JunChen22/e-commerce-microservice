package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.service.PMS.impl.ReviewServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "Review controller", description = "Review Controller")
@RequestMapping("/review")
public class ReviewAggregate {

    private final ReviewServiceImpl reviewService;

    @Autowired
    public ReviewAggregate(ReviewServiceImpl reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/detail/{reviewId}")
    @ApiOperation(value = "get detail of a review")
    public Mono<ProductReview> getDetailReview(@PathVariable int reviewId) {
        return reviewService.getDetailReview(reviewId);
    }

    @GetMapping("/listAllProductReview/{productId}")
    @ApiOperation(value = "list all reviews for a product")
    public Flux<ProductReview> getProductReviews(@PathVariable int productId) {
        return reviewService.getProductReviews(productId);
    }

    @PostMapping("/create")
    @ApiOperation(value = "create review for a product")
    public Mono<ProductReview> createProductReview(@RequestBody ProductReview newReview) {
        return reviewService.createProductReview(newReview);
    }

    @PostMapping("/update")
    @ApiOperation(value = "update a review")
    public Mono<ProductReview> updateProductReviews(@RequestBody ProductReview newReview) {
        return reviewService.updateProductReviews(newReview);
    }

    @DeleteMapping("/delete/{reviewId}")
    @ApiOperation(value = "Get product with page and size")
    public Mono<Void> deleteProductReviews(@PathVariable int reviewId) {
        return reviewService.deleteProductReviews(reviewId);
    }
}
