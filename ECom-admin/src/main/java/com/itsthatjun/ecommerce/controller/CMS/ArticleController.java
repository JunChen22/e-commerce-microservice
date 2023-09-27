package com.itsthatjun.ecommerce.controller.CMS;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import com.itsthatjun.ecommerce.dto.cms.event.CmsAdminArticleEvent;
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

    private final WebClient webClient;

    private final StreamBridge streamBridge;

    private final Scheduler publishEventScheduler;

    private final String CMS_SERVICE_URL = "http://cms/article";

    @Autowired
    public ArticleController(WebClient.Builder webClient, StreamBridge streamBridge,
                             @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.streamBridge = streamBridge;
        this.publishEventScheduler = publishEventScheduler;
    }

    @GetMapping("/all")
    @ApiOperation(value = "Get all the articles")
    public Flux<ArticleInfo> getAllArticle() {
        String url = CMS_SERVICE_URL + "/article/all";

        return webClient.get().uri(url).retrieve().bodyToFlux(ArticleInfo.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Flux.empty());
    }

    @GetMapping("/{articleId}")
    @ApiOperation(value = "Get a specific article by article id")
    public Mono<ArticleInfo> getArticle(@PathVariable int articleId) {
        String url = CMS_SERVICE_URL + "/article/" + articleId;

        return webClient.get().uri(url).retrieve().bodyToMono(ArticleInfo.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_admin-content')")
    @ApiOperation(value = "create an article(buyer's guide, comparison, and etc)")
    public void createArticle(@RequestBody ArticleInfo articleInfo) {
        Mono.fromRunnable(() -> sendMessage("article-out-0", new CmsAdminArticleEvent(CREATE, articleInfo, null)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @PostMapping("/update")
    @PreAuthorize("hasRole('ROLE_admin-content')")
    @ApiOperation(value = "update an article and it's content")
    public void updateArticle(@RequestBody ArticleInfo articleInfo) {
        int articleId = articleInfo.getArticle().getId();
        Mono.fromRunnable(() -> sendMessage("article-out-0", new CmsAdminArticleEvent(UPDATE, articleInfo, articleId)))
                .subscribeOn(publishEventScheduler).subscribe();
    }

    @DeleteMapping("/delete/{articleId}")
    @PreAuthorize("hasRole('ROLE_admin-content')")
    @ApiOperation(value = "delete article and it's related content(QA, videos, and images)")
    public void deleteArticle(@PathVariable int articleId) {
        Mono.fromRunnable(() -> sendMessage("article-out-0", new CmsAdminArticleEvent(DELETE, null, articleId)))
                .subscribeOn(publishEventScheduler).subscribe();
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
