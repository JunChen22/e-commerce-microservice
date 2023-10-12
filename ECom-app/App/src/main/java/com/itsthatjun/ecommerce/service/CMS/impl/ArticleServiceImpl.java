package com.itsthatjun.ecommerce.service.CMS.impl;

import com.itsthatjun.ecommerce.dto.Articles;
import com.itsthatjun.ecommerce.service.CMS.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final WebClient webClient;

    private final String CMS_SERVICE_URL = "http://cms/article";

    @Autowired
    public ArticleServiceImpl(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @Override
    public Flux<Articles> getAllArticle() {
        String url = CMS_SERVICE_URL + "/all";
        LOG.debug("Will call the getAllArticle API on URL: {}", url);

       return webClient.get().uri(url).retrieve().bodyToFlux(Articles.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<Articles> getArticle(int articleId) {
        String url = CMS_SERVICE_URL + "/" + articleId;
        LOG.debug("Will call the getArticle API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(Articles.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }
}
