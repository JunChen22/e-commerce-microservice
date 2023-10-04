package com.itsthatjun.ecommerce.controller.CMS;

import com.itsthatjun.ecommerce.dto.Articles;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@RestController
@Api(tags = "content related", description = "aggregate content related services")
@RequestMapping("/article")
public class ContentAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ContentAggregate.class);

    private final WebClient webClient;

    private final String CMS_SERVICE_URL = "http://cms/article";

    @Autowired
    public ContentAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @GetMapping("/all")
    @Cacheable(value = "articlesCache", key = "'getAllArticle'")
    @ApiOperation(value = "Get all articles")
    public List<Articles> getAllArticle() {
        String url = CMS_SERVICE_URL + "/all";
        LOG.debug("Will call the getAllArticle API on URL: {}", url);

        Flux<Articles> articlesFlux = webClient.get().uri(url)
                .retrieve()
                .bodyToFlux(Articles.class)
                .log(LOG.getName(), FINE)
                .onErrorResume(error -> Flux.empty());

        List<Articles> articlesList = articlesFlux.collectList().block();

        return articlesList;
    }

    @GetMapping("/{articleId}")
    public Mono<Articles> getArticle(@PathVariable int articleId) {
        String url = CMS_SERVICE_URL + "/" + articleId;
        LOG.debug("Will call the getArticle API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(Articles.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }
}
