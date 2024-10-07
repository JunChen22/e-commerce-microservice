package com.itsthatjun.ecommerce.controller.CMS;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import com.itsthatjun.ecommerce.service.CMS.impl.ArticleServiceImpl;
import io.swagger.annotations.ApiOperation;
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

    @GetMapping("/listAll")
    @ApiOperation(value = "Get all articles")
    public Flux<ArticleInfo> listAllArticle() {
        return articleService.listAllArticle();
    }

    @GetMapping("/{articleId}")
    @ApiOperation(value = "Get a article")
    public Mono<ArticleInfo> getArticle(@PathVariable int articleId,
                                        @RequestParam(value = "delay", required = false, defaultValue = "0") int delay,
                                        @RequestParam(value = "faultPercent", required = false, defaultValue = "0") int faultPercent) {
        return articleService.getArticle(articleId, delay, faultPercent);
    }
}
