package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.mbg.model.Review;
import com.itsthatjun.ecommerce.service.impl.ReviewServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@Api(tags = "Product related", description = "CRUD a specific product reviews")
public class ReviewController {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewServiceImpl reviewService;

    private final Scheduler scheduler;

    @Autowired
    public ReviewController(ReviewServiceImpl reviewService,
                            @Qualifier("scheduler") Scheduler scheduler) {
        this.reviewService = reviewService;
        this.scheduler = scheduler;
    }

    @GetMapping("/detail/{reviewId}")
    @ApiOperation(value = "get detail of a review")
    public Mono<ProductReview> getDetailReview(@PathVariable int reviewId) {
        return reviewService.getDetailReview(reviewId).subscribeOn(scheduler);
    }

    @GetMapping("/getAllProductReview/{productId}")
    @ApiOperation(value = "get all reviews for a product")
    public Flux<ProductReview> getProductReviews(@PathVariable int productId) {
        return reviewService.listProductAllReview(productId).subscribeOn(scheduler);
    }
    
    @PostMapping("/create")
    @ApiOperation(value = "create review for a product")
    public ProductReview createProductReview(@RequestBody ProductReview newReview, @RequestParam int userId) {
        reviewService.createReview(newReview.getReview(), newReview.getPicturesList(), userId);
        return newReview;
    }

    @PostMapping("/update")
    @ApiOperation(value = "update a review")
    public Mono<Review> updateProductReviews(@RequestBody ProductReview newReview, @RequestParam int userId) {
        return reviewService.updateReview(newReview.getReview(), newReview.getPicturesList(), userId);
    }

    @DeleteMapping("/delete/{reviewId}")
    @ApiOperation(value = "Get product with page and size")
    public Mono<Void> deleteProductReviews(@PathVariable int reviewId, @RequestParam int userId) {
        return reviewService.deleteReview(reviewId, userId);
    }


    @GetMapping("/admin/getAllReviewByUser/{userId}")
    @ApiOperation(value = "get all reviews for a product")
    public Flux<ProductReview> getAllReviewByUser(@PathVariable int userId) {
        return reviewService.listAllReviewByUser(userId);
    }

    @PostMapping("/admin/create")
    @ApiOperation(value = "create review for a product")
    public Mono<Review> adminCreateProductReview(@RequestBody ProductReview newReview) {
        return reviewService.adminCreateReview(newReview.getReview(), newReview.getPicturesList());
    }

    @PostMapping("/admin/update")
    @ApiOperation(value = "update a review")
    public Mono<Review> adminUpdateProductReviews(@RequestBody ProductReview newReview) {
        return reviewService.adminUpdateReview(newReview.getReview(), newReview.getPicturesList());

    }

    @DeleteMapping("/admin/delete/{reviewId}")
    @ApiOperation(value = "Get product with page and size")
    public Mono<Void> adminDeleteProductReviews(@PathVariable int reviewId) {
        return reviewService.adminDeleteReview(reviewId);
    }
}
