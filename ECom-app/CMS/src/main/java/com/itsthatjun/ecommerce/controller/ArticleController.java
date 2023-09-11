package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.ArticleInfo;
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
    public List<ArticleInfo> getAllArticle() {
        return articleService.getAllArticles();
    }

    @GetMapping("/{articleId}")
    @ApiOperation(value = "")
    public ArticleInfo getArticle(@PathVariable int articleId) {
        return articleService.getArticle(articleId);
    }

    @PostMapping("/admin/create")
    @ApiOperation(value = "create an article(buyer's guide, comparison, and etc)")
    public ArticleInfo createArticle(@RequestBody ArticleInfo articles) {
        return articleService.createArticle(articles);
    }

    @PostMapping("/admin/update")
    @ApiOperation(value = "update an article and it's content")
    public ArticleInfo updateArticle(@RequestBody ArticleInfo articles) {
        articleService.updateArticle(articles);
        return articles;
    }

    @DeleteMapping("/admin/delete/{articleId}")
    @ApiOperation(value = "delete article and it's related content(QA, videos, and images)")
    public void deleteArticle(@PathVariable int articleId) {
        articleService.deleteArticle(articleId);
    }
}
