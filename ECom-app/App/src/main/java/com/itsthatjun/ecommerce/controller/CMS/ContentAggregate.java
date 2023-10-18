package com.itsthatjun.ecommerce.controller.CMS;

import com.itsthatjun.ecommerce.dto.Articles;
import com.itsthatjun.ecommerce.service.CMS.impl.ArticleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@Api(tags = "content related", description = "aggregate content related services")
@RequestMapping("/article")
public class ContentAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ContentAggregate.class);

    private final ArticleServiceImpl articleService;

    @Autowired
    public ContentAggregate(ArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/all")
    @Cacheable(value = "articlesCache", key = "'getAllArticle'")
    @ApiOperation(value = "Get all articles")
    public List<Articles> getAllArticle() {
        Flux<Articles> articlesFlux = articleService.getAllArticle();

        List<Articles> articlesList = articlesFlux.collectList().block();

        return articlesList;
    }

    @GetMapping("/{articleId}")
    @ApiOperation(value = "Get a article")
    public Mono<Articles> getArticle(@PathVariable int articleId,
                                     @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
                                     @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) {
        return articleService.getArticle(articleId, delay, faultPercent);
    }
}
