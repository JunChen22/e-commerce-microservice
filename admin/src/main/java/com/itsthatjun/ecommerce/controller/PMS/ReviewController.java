package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductReview;
import com.itsthatjun.ecommerce.service.PMS.impl.ReviewServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/review")
@PreAuthorize("hasRole('ROLE_admin_product')")
@Tag(name = "Product related", description = "CRUD a specific product reviews")
public class ReviewController {

    private final ReviewServiceImpl reviewService;

    @Autowired
    public ReviewController(ReviewServiceImpl reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/listAllProductReview/{productId}")
    @ApiOperation(value = "get all reviews for a product")
    public Flux<ProductReview> getProductReviews(@PathVariable int productId) {
        return reviewService.getProductReviews(productId);
    }

    @GetMapping("/listAllReviewByUser/{useId}")
    @ApiOperation(value = "get all reviews made a user")
    public Flux<ProductReview> getProductReviewsByUser(@PathVariable int useId) {
        return reviewService.getProductReviewsByUser(useId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasPermission('review:create')")
    @ApiOperation(value = "create review for a product")
    public Mono<ProductReview> createProductReview(@RequestBody ProductReview review) {
        return reviewService.createProductReview(review);
    }

    @PostMapping("/update")
    @PreAuthorize("hasPermission('review:update')")
    @ApiOperation(value = "update a review")
    public Mono<ProductReview> updateProductReviews(@RequestBody ProductReview updatedReview) {
        return reviewService.updateProductReviews(updatedReview);
    }

    @DeleteMapping("/delete/{reviewId}")
    @PreAuthorize("hasPermission('review:delete')")
    @ApiOperation(value = "Get product with page and size")
    public Mono<Void> deleteProductReviews(@PathVariable int reviewId) {
        return reviewService.deleteProductReviews(reviewId);
    }
}
