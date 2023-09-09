package com.itsthatjun.ecommerce.controller.UMS;

import com.itsthatjun.ecommerce.controller.PMS.ReviewAggregate;
import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Scheduler;

import java.util.List;

@RestController
@Api(tags = "", description = "")
@RequestMapping("/user")
public class UserAggregate {
    private static final Logger LOG = LoggerFactory.getLogger(UserAggregate.class);

    private final WebClient webClient;
    private final Scheduler publishEventScheduler;

    @Value("${app.UMS-service.host}")
    String userServiceURL;

    @Value("${app.UMS-service.port}")
    int port;

    @Autowired
    public UserAggregate(WebClient.Builder  webClient, @Qualifier("publishEventScheduler") Scheduler publishEventScheduler) {
        this.webClient = webClient.build();
        this.publishEventScheduler = publishEventScheduler;
    }
}
