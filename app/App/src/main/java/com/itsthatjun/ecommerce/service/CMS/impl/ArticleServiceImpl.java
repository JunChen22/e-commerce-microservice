package com.itsthatjun.ecommerce.service.CMS.impl;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import com.itsthatjun.ecommerce.service.CMS.ArticleService;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.ServiceUnavailableException;
import java.net.URI;

import static java.util.logging.Level.FINE;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final WebClient webClient;

    private final String CMS_SERVICE_URL = "http://cms/article";

    @Autowired
    public ArticleServiceImpl(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Flux<ArticleInfo> listAllArticle() {
        String url = CMS_SERVICE_URL + "/all";
        LOG.debug("Will call the getAllArticle API on URL: {}", url);

       return webClient.get().uri(url).retrieve().bodyToFlux(ArticleInfo.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    @TimeLimiter(name = "genericTimeLimiter")
    @Retry(name = "genericRetry")
    @RateLimiter(name = "genericRateLimiter")
    @CircuitBreaker(name = "genericCircuitBreaker", fallbackMethod = "getArticleFallbackValue")
    public Mono<ArticleInfo> getArticle(int articleId, int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString(CMS_SERVICE_URL + "/{articleId}?delay={delay}&faultPercent={faultPercent}")
                .build(articleId, delay, faultPercent);
        LOG.debug("Will call the getArticle API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(ArticleInfo.class)
                .log(LOG.getName(), FINE)
                .onErrorResume(error -> Mono.empty());
    }

    private Mono<ArticleInfo> getArticleFallbackValue(int articleId, int delay, int faultPercent, CallNotPermittedException ex) {
        LOG.warn("Creating a fail-fast fallback article for articleId = {}, delay = {}, faultPercent = {} and exception = {} ",
                articleId, delay, faultPercent, ex.toString());
        return Mono.error(new ServiceUnavailableException("The service is currently unavailable."));
    }
}
