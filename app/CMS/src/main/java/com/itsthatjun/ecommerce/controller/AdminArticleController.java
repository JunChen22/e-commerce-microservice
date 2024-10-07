package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.admin.AdminArticleInfo;
import com.itsthatjun.ecommerce.service.impl.AdminArticleServiceImpl;
import io.swagger.annotations.ApiOperation;
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

    @GetMapping("/listAll")
    @ApiOperation("get all articles")
    public Flux<AdminArticleInfo> listAllArticle() {
        return articleService.listAllArticles();
    }

    @GetMapping("/{articleId}")
    @ApiOperation("")
    public Mono<AdminArticleInfo> getArticle(@PathVariable int articleId,
                                        @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
                                        @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) {
        return articleService.getArticle(articleId, delay, faultPercent);
    }
}
