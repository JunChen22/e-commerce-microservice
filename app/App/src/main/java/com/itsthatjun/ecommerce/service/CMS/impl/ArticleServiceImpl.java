package com.itsthatjun.ecommerce.service.CMS.impl;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import com.itsthatjun.ecommerce.service.CMS.ArticleService;
import com.itsthatjun.ecommerce.service.impl.RedisServiceImpl;
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
import java.util.List;
import java.util.logging.Level;

import static java.util.logging.Level.FINE;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final RedisServiceImpl redisService;

    private final WebClient webClient;

    private final String CMS_SERVICE_URL = "http://cms/article";

    @Autowired
    public ArticleServiceImpl(RedisServiceImpl redisService, WebClient webClient) {
        this.redisService = redisService;
        this.webClient = webClient;
    }

    @Override
    public Flux<ArticleInfo> listAllArticles() {
        String url = CMS_SERVICE_URL + "/listAll";
        LOG.debug("Will call the listArticles API on URL: {}", url);

        String redisKey = "articles:listAll";

        return redisService.get(redisKey)
                .flatMapMany(cachedArticles -> {
                    // Cache hit: Deserialize and return cached articles
                    LOG.debug("Cache hit for key: {}", redisKey);
                    if (cachedArticles instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<ArticleInfo> articleList = (List<ArticleInfo>) cachedArticles;
                        return Flux.fromIterable(articleList);
                    } else {
                        LOG.error("Cache data is invalid for key: {}", redisKey);
                        return Flux.empty();
                    }
                })
                .switchIfEmpty(
                        // Cache miss: Fetch from external service and store in Redis
                        webClient.get()
                                .uri(url)
                                .retrieve()
                                .bodyToFlux(ArticleInfo.class)
                                .collectList() // Convert Flux to List for caching
                                .flatMapMany(articles -> {
                                    LOG.debug("Cache miss for key: {}, storing data in cache", redisKey);
                                    return redisService.set(redisKey, articles)
                                            .thenMany(Flux.fromIterable(articles));
                                })
                )
                .onErrorResume(error -> {
                    LOG.error("Error fetching articles from cache or external service: ", error);
                    return Flux.empty(); // Fallback to empty Flux on error
                })
                .log(LOG.getName(), Level.FINE);
    }

    @Override
    public Flux<ArticleInfo> listArticles(int page, int size) {
        String url = CMS_SERVICE_URL + "/list" + "?page=" + page + "&size=" + size;
        LOG.debug("Will call the listArticles with pagination API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(ArticleInfo.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    @TimeLimiter(name = "genericTimeLimiter")
    @Retry(name = "genericRetry")
    @RateLimiter(name = "genericRateLimiter")
    @CircuitBreaker(name = "genericCircuitBreaker", fallbackMethod = "getArticleFallbackValue")
    public Mono<ArticleInfo> getArticle(String slug, int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString(CMS_SERVICE_URL + "/{slug}?delay={delay}&faultPercent={faultPercent}")
                .build(slug, delay, faultPercent);

        LOG.debug("Will call the getArticle API on URL: {}", url);

        return redisService.get(slug)
                .flatMap(cachedData -> {
                    LOG.debug("Cache hit for slug: {}", slug);
                    return Mono.just((ArticleInfo) cachedData);
                })
                .switchIfEmpty(
                        webClient.get().uri(url).retrieve().bodyToMono(ArticleInfo.class)
                                .flatMap(article -> {
                                    LOG.debug("Cache miss for slug: {}, storing in cache", slug);
                                    return redisService.set(slug, article)
                                            .thenReturn(article);
                                })
                                .onErrorResume(error -> {
                                    LOG.error("Error fetching article from external service: {}", error.getMessage());
                                    return Mono.empty();
                                })
                )
                .log(LOG.getName(), FINE);
    }

    private Mono<ArticleInfo> getArticleFallbackValue(String slug, int delay, int faultPercent, Throwable ex) {
        LOG.warn("Creating a fail-fast fallback article for slug = {}, delay = {}, faultPercent = {}, exception = {}",
                slug, delay, faultPercent, ex.toString());
        return Mono.error(new ServiceUnavailableException("The service is currently unavailable."));
    }
}
