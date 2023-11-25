package com.itsthatjun.ecommerce.controller.CMS;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import com.itsthatjun.ecommerce.service.CMS.impl.ArticleServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    @ApiOperation(value = "Get all articles")
    public List<ArticleInfo> getAllArticle() {
        Flux<ArticleInfo> articlesFlux = articleService.getAllArticle();

        List<ArticleInfo> articlesList = articlesFlux.collectList().block();

        return articlesList;
    }

    @GetMapping("/{articleId}")
    @ApiOperation(value = "Get a article")
    public Mono<ArticleInfo> getArticle(@PathVariable int articleId,
                                        @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
                                        @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) {
        return articleService.getArticle(articleId, delay, faultPercent);
    }
}
