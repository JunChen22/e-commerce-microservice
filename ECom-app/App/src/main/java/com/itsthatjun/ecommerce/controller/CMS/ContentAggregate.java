package com.itsthatjun.ecommerce.controller.CMS;

import com.itsthatjun.ecommerce.dto.Articles;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.health.Health;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.logging.Level.FINE;
import static reactor.core.publisher.Flux.empty;

@RestController
@Api(tags = "content related", description = "aggregate content related services")
@RequestMapping("/content")
public class ContentAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(ContentAggregate.class);

    private final WebClient webClient;

    private final String CMS_SERVICE_URL = "http://cms";

    @Autowired
    public ContentAggregate(@Qualifier("loadBalancedWebClientBuilder") WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @GetMapping("/article/nope")
    public String testController() {
        return "nope";
    }

    @PostMapping("/article/post")
    public String testpostController() {
        return "post";
    }



    @GetMapping("/article/all")
    public Flux<Articles> getAllArticle() {
        String url = CMS_SERVICE_URL + "/article/all";
        LOG.debug("Will call the getAllArticle API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToFlux(Articles.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/article/{articleId}")
    public Mono<Articles> getArticle(@PathVariable int articleId) {
        String url = CMS_SERVICE_URL + "/article/" + articleId;
        LOG.debug("Will call the getArticle API on URL: {}", url);

        return webClient.get().uri(url).retrieve().bodyToMono(Articles.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }
}
