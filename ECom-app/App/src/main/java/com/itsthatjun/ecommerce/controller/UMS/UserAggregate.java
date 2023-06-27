package com.itsthatjun.ecommerce.controller.UMS;

import com.itsthatjun.ecommerce.mbg.model.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags = "", description = "")
@RequestMapping("/user")
public class UserAggregate {
    private final RestTemplate restTemplate;

    @Value("${app.OMS-service.host}")
    String orderServiceURL;

    @Value("${app.OMS-service.port}")
    int port;

    @Autowired
    public UserAggregate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

}
