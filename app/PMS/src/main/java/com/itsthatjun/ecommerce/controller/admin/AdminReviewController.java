package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.ProductReview;
import com.itsthatjun.ecommerce.service.admin.AdminReviewServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/review/admin")
@Tag(name = "Review related", description = "Review management service controller")
public class AdminReviewController {

    private final AdminReviewServiceImpl reviewService;

    @Autowired
    public AdminReviewController(AdminReviewServiceImpl reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "List all reviews for a product",
            description = "List all reviews for a product by its SKU code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of reviews of a product"),
            @ApiResponse(responseCode = "404", description = "Product not found")})
    @GetMapping("/listAllReviewByUser/{useId}")
    public Flux<ProductReview> listProductReviewsByUser(@PathVariable int useId) {
        return reviewService.listAllReviewByUser(useId);
    }
}
