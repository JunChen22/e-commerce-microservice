package com.itsthatjun.ecommerce.controller.CMS;

import com.itsthatjun.ecommerce.dto.cms.AdminArticleInfo;
import com.itsthatjun.ecommerce.service.CMS.impl.ArticleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/article")
@PreAuthorize("hasRole('ROLE_admin_content')")
@Api(tags = "content related", description = "CRUD articles like buyer's guide, product comparison, and MISC articles")
public class ArticleController {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleController.class);

    private final ArticleServiceImpl articleService;

    @Autowired
    public ArticleController(ArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/all")
    @ApiOperation(value = "Get all the articles")
    public Flux<AdminArticleInfo> getAllArticle() {
        return articleService.getAllArticle();
    }

    @GetMapping("/{articleId}")
    @ApiOperation(value = "Get a specific article by article id")
    public Mono<AdminArticleInfo> getArticle(@PathVariable int articleId) {
        return articleService.getArticle(articleId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasPermission('content:create')")
    @ApiOperation(value = "create an article(buyer's guide, comparison, and etc)")
    public Mono<AdminArticleInfo> createArticle(@RequestBody AdminArticleInfo articleInfo) {
        return articleService.createArticle(articleInfo);
    }

    @PostMapping("/update")
    @PreAuthorize("hasPermission('content:update')")
    @ApiOperation(value = "update an article and it's content")
    public Mono<AdminArticleInfo> updateArticle(@RequestBody AdminArticleInfo articleInfo) {
        return articleService.updateArticle(articleInfo);
    }

    @DeleteMapping("/delete/{articleId}")
    @PreAuthorize("hasPermission('content:delete')")
    @ApiOperation(value = "delete article and it's related content(QA, videos, and images)")
    public Mono<Void> deleteArticle(@PathVariable int articleId) {
        return articleService.deleteArticle(articleId);
    }
}
