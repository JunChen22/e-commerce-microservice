package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.ArticleInfo;
import com.itsthatjun.ecommerce.enums.status.PublishStatus;
import com.itsthatjun.ecommerce.model.entity.Article;
import com.itsthatjun.ecommerce.repository.ArticleRepository;
import com.itsthatjun.ecommerce.service.impl.ArticleServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/article")
@Tag(name = "content related", description = "CRUD articles like buyer's guide, product comparison, and MISC articles")
public class ArticleController {

    private final ArticleServiceImpl articleService;

    @Autowired
    public ArticleController(ArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    @Operation(summary = "get all articles",
            description = "get all articles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all articles"),
            @ApiResponse(responseCode = "404", description = "No articles found")})
    @GetMapping(value = "/listAll", produces = "application/json")
    public Flux<ArticleInfo> listAllArticles() {
        return articleService.listAllArticles();
    }

    @Operation(summary = "get all articles with pagination",
            description = "get all articles with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all articles"),
            @ApiResponse(responseCode = "404", description = "No articles found")})
    @GetMapping(value = "/list", produces = "application/json")
    public Flux<ArticleInfo> listArticles(@RequestParam int page, @RequestParam int size) {
        return articleService.listArticles(page, size);
    }

    @Operation(summary = "Get article",
            description = "Get article based on slug, added delay and fault percentage for circuit breaker testing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Article not found")})
    @GetMapping(value = "/{slug}", produces = "application/json")
    public Mono<ArticleInfo> getArticle(@PathVariable String slug,
                                        @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
                                        @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) {
        return articleService.getArticleBySlug(slug, delay, faultPercent);
    }
}
