package com.itsthatjun.ecommerce.controller.CMS;


import com.itsthatjun.ecommerce.dto.Articles;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @Value("${app.CMS-service.host}")
    String contentServiceURL;
    @Value("${app.CMS-service.port}")
    int port;

    @Autowired
    public ContentAggregate(WebClient.Builder webClient) {
        this.webClient = webClient.build();
    }

    @GetMapping("/article/all")
    public Flux<Articles> getAllArticle() {
        String url = "http://" + contentServiceURL + ":" + port + "/article/all";

        return webClient.get().uri(url).retrieve().bodyToFlux(Articles.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> empty());
    }

    @GetMapping("/article/{articleId}")
    public Mono<Articles> getArticle(@PathVariable int articleId) {
        String url = "http://" + contentServiceURL + ":" + port + "/article/" + articleId;
        return webClient.get().uri(url).retrieve().bodyToMono(Articles.class)
                .log(LOG.getName(), FINE).onErrorResume(error -> Mono.empty());
    }
}
