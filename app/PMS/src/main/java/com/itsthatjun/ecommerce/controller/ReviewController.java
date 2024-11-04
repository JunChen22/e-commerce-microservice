package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.service.impl.ReviewServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/review")
@Tag(name = "Product related", description = "CRUD a specific product reviews")
public class ReviewController {

    private final ReviewServiceImpl reviewService;

    @Autowired
    public ReviewController(ReviewServiceImpl reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/listAllProductReview/{productId}")
    @ApiOperation(value = "list all reviews for a product")
    public Flux<ProductReview> listProductReviews(@PathVariable int productId) {
        return reviewService.listProductAllReview(productId);
    }

    @GetMapping("/detail/{reviewId}")
    @ApiOperation(value = "get detail of a review")
    public Mono<ProductReview> getDetailReview(@PathVariable int reviewId) {
        return reviewService.getDetailReview(reviewId);
    }
}
