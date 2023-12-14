package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductReview;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.service.PMS.impl.ReviewServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/review")
@Api(tags = "Product related", description = "CRUD a specific product reviews")
public class ReviewController {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewController.class);

    private final ReviewServiceImpl reviewService;

    @Autowired
    public ReviewController(ReviewServiceImpl reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/getAllProductReview/{productId}")
    @ApiOperation(value = "get all reviews for a product")
    public Flux<ProductReview> getProductReviews(@PathVariable int productId) {
        return reviewService.getProductReviews(productId);
    }

    @GetMapping("/getAllReviewByUser/{useId}")
    @ApiOperation(value = "get all reviews made a user")
    public Flux<ProductReview> getProductReviewsByUser(@PathVariable int useId) {
        return reviewService.getProductReviewsByUser(useId);
    }

    @PostMapping("/create")
    @ApiOperation(value = "create review for a product")
    public Mono<ProductReview> createProductReview(@RequestBody ProductReview review) {
        String operatorName = getAdminName();
        return reviewService.createProductReview(review, operatorName);
    }

    @PostMapping("/update")
    @ApiOperation(value = "update a review")
    public Mono<ProductReview> updateProductReviews(@RequestBody ProductReview updatedReview) {
        String operatorName = getAdminName();
        return reviewService.updateProductReviews(updatedReview, operatorName);
    }

    @DeleteMapping("/delete/{reviewId}")
    @ApiOperation(value = "Get product with page and size")
    public Mono<Void> deleteProductReviews(@PathVariable int reviewId) {
        String operatorName = getAdminName();
        return reviewService.deleteProductReviews(reviewId, operatorName);
    }

    private String getAdminName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetail userDetail = (CustomUserDetail) authentication.getPrincipal();
        String adminName = userDetail.getAdmin().getName();
        return adminName;
    }
}
