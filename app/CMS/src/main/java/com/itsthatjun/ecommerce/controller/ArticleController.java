package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.ArticleInfo;
import com.itsthatjun.ecommerce.service.impl.ArticleServiceImpl;
import io.swagger.annotations.ApiOperation;
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

    @GetMapping("/listAll")
    @ApiOperation("get all articles")
    public Flux<ArticleInfo> listAllArticle() {
        return articleService.listAllArticles();
    }

    @GetMapping("/{articleId}")
    @ApiOperation("")
    public Mono<ArticleInfo> getArticle(@PathVariable int articleId,
                                        @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
                                        @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) {
        return articleService.getArticle(articleId, delay, faultPercent);
    }
}
