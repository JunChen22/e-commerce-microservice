package com.itsthatjun.ecommerce.controller.CMS;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import com.itsthatjun.ecommerce.dto.cms.event.CmsAdminArticleEvent;
import com.itsthatjun.ecommerce.service.CMS.impl.ArticleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.cms.event.CmsAdminArticleEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@RequestMapping("/article")
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
    public Flux<ArticleInfo> getAllArticle() {
        return articleService.getAllArticle();
    }

    @GetMapping("/{articleId}")
    @ApiOperation(value = "Get a specific article by article id")
    public Mono<ArticleInfo> getArticle(@PathVariable int articleId) {
        return articleService.getArticle(articleId);
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_admin-content')")
    @ApiOperation(value = "create an article(buyer's guide, comparison, and etc)")
    public Mono<ArticleInfo> createArticle(@RequestBody ArticleInfo articleInfo) {
        return articleService.createArticle(articleInfo);
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ROLE_admin-content')")
    @ApiOperation(value = "update an article and it's content")
    public Mono<ArticleInfo> updateArticle(@RequestBody ArticleInfo articleInfo) {
        return articleService.updateArticle(articleInfo);
    }

    @DeleteMapping("/delete/{articleId}")
    @PreAuthorize("hasRole('ROLE_admin-content')")
    @ApiOperation(value = "delete article and it's related content(QA, videos, and images)")
    public Mono<Void> deleteArticle(@PathVariable int articleId) {
        return articleService.deleteArticle(articleId);
    }
}
