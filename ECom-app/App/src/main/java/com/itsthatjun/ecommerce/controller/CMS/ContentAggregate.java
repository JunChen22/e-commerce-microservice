package com.itsthatjun.ecommerce.controller.CMS;

import com.itsthatjun.ecommerce.dto.Articles;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@Api(tags = "content related", description = "aggregate content related services")
@RequestMapping("/content")
public class ContentAggregate {
    private final RestTemplate restTemplate;

    @Value("${app.CMS-service.host}")
    String contentServiceURL;

    @Value("${app.CMS-service.port}")
    int port;

    @Autowired
    public ContentAggregate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/article/all")
    public List<Articles> getAllArticle() {
        String url = "http://" + contentServiceURL + ":" + port + "/article/all";
        System.out.println("at aggre get:" + url );
        List<Articles> result = restTemplate.exchange(url, HttpMethod.GET,
                null, new ParameterizedTypeReference<List<Articles>>(){})
                .getBody();
        return result;
    }

    @GetMapping("/article/{articleId}")
    public Articles getArticle(@PathVariable int articleId) {
        String url = "http://" + contentServiceURL + ":" + port + "/article/" + articleId;
        System.out.println("at aggre get:" + url );
        return restTemplate.getForObject(url, Articles.class);
    }
}
