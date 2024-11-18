package com.itsthatjun.ecommerce.controller.PMS;

import com.itsthatjun.ecommerce.dto.pms.ProductReview;
import com.itsthatjun.ecommerce.service.PMS.impl.ReviewServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "get detail of a review", description = "get detail of a review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get detail of a review"),
            @ApiResponse(responseCode = "404", description = "No review found")})
    @GetMapping("/detail/{reviewId}")
    public Mono<ProductReview> getDetailReview(@PathVariable int reviewId) {
        return reviewService.getDetailReview(reviewId);
    }

    @Operation(summary = "list all reviews for a product", description = "list all reviews for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "list all reviews for a product"),
            @ApiResponse(responseCode = "404", description = "No review found")})
    @GetMapping("/listAllProductReview/{skuCode}")
    public Flux<ProductReview> getProductReviews(@PathVariable String skuCode) {
        return reviewService.getProductReviews(skuCode);
    }

    @Operation(summary = "create review for a product", description = "create review for a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "create review for a product"),
            @ApiResponse(responseCode = "404", description = "No review found")})
    @PostMapping("/create")
    public Mono<ProductReview> createProductReview(@RequestBody ProductReview newReview) {
        return reviewService.createProductReview(newReview);
    }

    @Operation(summary = "update a review", description = "update a review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "update a review"),
            @ApiResponse(responseCode = "404", description = "No review found")})
    @PostMapping("/update")
    public Mono<ProductReview> updateProductReviews(@RequestBody ProductReview newReview) {
        return reviewService.updateProductReviews(newReview);
    }

    @Operation(summary = "delete a review", description = "delete a review")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "delete a review"),
            @ApiResponse(responseCode = "404", description = "No review found")})
    @DeleteMapping("/delete/{skuCode}")
    public Mono<Void> deleteProductReviews(@PathVariable String skuCode) {
        return reviewService.deleteProductReviews(skuCode);
    }
}
