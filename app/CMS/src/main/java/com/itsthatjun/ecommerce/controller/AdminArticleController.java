package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.model.AdminArticleInfo;
import com.itsthatjun.ecommerce.service.impl.AdminArticleServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/article/admin")
@Tag(name = "content related", description = "CRUD articles like buyer's guide, product comparison, and MISC articles")
public class AdminArticleController {

    private final AdminArticleServiceImpl articleService;

    @Autowired
    public AdminArticleController(AdminArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    @Operation(summary = "get all articles",
            description = "get all articles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get all articles")
    })
    @GetMapping(value = "/listAll", produces = "application/json")
    public Flux<AdminArticleInfo> listAllArticles() {
        return articleService.listAllArticles();
    }

    @Operation(summary = "get article",
            description = "get article based on id, added delay and fault percentage for circuit breaker testing")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "get article"),
            @ApiResponse(responseCode = "404", description = "article not found")
    })
    @GetMapping(value = "/{articleId}", produces = "application/json")
    public Mono<AdminArticleInfo> getArticle(@PathVariable int articleId,
                                        @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
                                        @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) {
        return articleService.getArticle(articleId, delay, faultPercent);
    }
}
