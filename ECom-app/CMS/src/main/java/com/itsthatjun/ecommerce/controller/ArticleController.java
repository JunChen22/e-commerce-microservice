package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.Articles;
import com.itsthatjun.ecommerce.service.impl.ArticleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/article")
@Api(tags = "content related", description = "CRUD articles like buyer's guide, product comparison, and MISC articles")
public class ArticleController {

    private final ArticleServiceImpl articleService;

    @Autowired
    public ArticleController(ArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/all")
    @ApiOperation(value = "get all articles")
    public List<Articles> getAllArticle() {
        return articleService.getAllArticles();
    }

    @GetMapping("/{articleId}")
    @ApiOperation(value = "")
    public Articles getArticle(@PathVariable int articleId) {
        return articleService.getArticle(articleId);
    }
}
