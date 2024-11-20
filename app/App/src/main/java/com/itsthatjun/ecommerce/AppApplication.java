package com.itsthatjun.ecommerce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class AppApplication {

    private static final Logger LOG = LoggerFactory.getLogger(AppApplication.class);

    // Netflix Ribbon Load Balancer replaced with Spring Cloud Load Balancer
    private final ReactorLoadBalancerExchangeFilterFunction lbFunction;

    @Autowired
    public AppApplication(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.lbFunction = lbFunction;
    }

    @Bean   // turn all the webclient to load balanced when sending, if there's more than one. it doesn't work with Kubernetes
    @Profile("eureka")
    public WebClient loadBalancedWebClient(WebClient.Builder builder) {
        return builder.filter(lbFunction).build();
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
