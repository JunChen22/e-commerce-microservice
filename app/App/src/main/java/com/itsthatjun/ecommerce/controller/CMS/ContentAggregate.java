package com.itsthatjun.ecommerce.controller.CMS;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import com.itsthatjun.ecommerce.service.CMS.impl.ArticleServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "content related", description = "aggregate content related services")
@RequestMapping("/article")
public class ContentAggregate {

    private final ArticleServiceImpl articleService;

    @Autowired
    public ContentAggregate(ArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    @Operation(summary = "Get all articles", description = "Get all articles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all articles"),
            @ApiResponse(responseCode = "404", description = "No articles found")})
    @GetMapping("/listAll")
    public Flux<ArticleInfo> listAllArticles() {
        return articleService.listAllArticles();
    }

    @Operation(summary = "Get all articles with pagination", description = "Get all articles with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all articles"),
            @ApiResponse(responseCode = "404", description = "No articles found")})
    @GetMapping("/list")
    public Flux<ArticleInfo> listArticles(@RequestParam int page, @RequestParam int size) {
        return articleService.listArticles(page, size);
    }

    @Operation(summary = "Get a article", description = "Get a article based on slug, added delay and fault percentage for circuit breaker testing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Article retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Article not found")})
    @GetMapping("/{slug}")
    public Mono<ArticleInfo> getArticle(@PathVariable String slug,
                                        @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
                                        @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) {
        return articleService.getArticle(slug, delay, faultPercent);
    }
}
