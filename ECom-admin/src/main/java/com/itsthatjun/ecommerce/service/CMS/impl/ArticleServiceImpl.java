package com.itsthatjun.ecommerce.service.CMS.impl;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import com.itsthatjun.ecommerce.dto.cms.event.CmsAdminArticleEvent;
import com.itsthatjun.ecommerce.mbg.model.Article;
import com.itsthatjun.ecommerce.service.CMS.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import static com.itsthatjun.ecommerce.dto.cms.event.CmsAdminArticleEvent.Type.*;
import static java.util.logging.Level.FINE;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String CMS_SERVICE_URL = "http://cms/article";

    @Autowired
    public ArticleServiceImpl(WebClient.Builder webClient, StreamBridge streamBridge,
                             @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @Override
    public Flux<ArticleInfo> getAllArticle() {
        String url = CMS_SERVICE_URL + "/all";
        LOG.debug("Will call the getAllArticle API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(ArticleInfo.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @Override
    public Mono<ArticleInfo> getArticle(int articleId) {
        String url = CMS_SERVICE_URL + "/" + articleId;
        LOG.debug("Will call the getArticle API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(ArticleInfo.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @Override
    public Mono<ArticleInfo> createArticle(ArticleInfo articleInfo, String operator) {
        return Mono.fromCallable(() -> {
            sendMessage("article-out-0", new CmsAdminArticleEvent(CREATE, articleInfo, operator));
            return articleInfo;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<ArticleInfo> updateArticle(ArticleInfo articleInfo, String operator) {
        return Mono.fromCallable(() -> {
            sendMessage("article-out-0", new CmsAdminArticleEvent(UPDATE, articleInfo, operator));
            return articleInfo;
        }).subscribeOn(publishEventScheduler);
    }

    @Override
    public Mono<Void> deleteArticle(int articleId, String operator) {
        return Mono.fromRunnable(() -> {
            Article article = new Article();
            article.setId(articleId);
            ArticleInfo articleInfo = new ArticleInfo();
            articleInfo.setArticle(article);

            sendMessage("article-out-0", new CmsAdminArticleEvent(DELETE, articleInfo, operator));
        }).subscribeOn(publishEventScheduler).then();
    }

    private void sendMessage(String bindingName, CmsAdminArticleEvent event) {
        LOG.debug("Sending a {} message to {}", event.getEventType(), bindingName);
        System.out.println("sending to binding: " + bindingName);
        Message message = MessageBuilder.withPayload(event)
                .setHeader("event-type", event.getEventType())
                .build();
        streamBridge.send(bindingName, message);
    }
}
