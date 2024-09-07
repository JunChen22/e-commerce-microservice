package com.itsthatjun.ecommerce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class AppApplication {

    private static final Logger LOG = LoggerFactory.getLogger(AppApplication.class);

    private final Integer threadPoolSize;

    private final Integer taskQueueSize;

    // Netflix Ribbon Load Balancer replaced with Spring Cloud Load Balancer
    private ReactorLoadBalancerExchangeFilterFunction lbFunction;

    @Autowired
    public AppApplication(
            @Value("${app.threadPoolSize:2}") Integer threadPoolSize,
            @Value("${app.taskQueueSize:100}") Integer taskQueueSize,
            ReactorLoadBalancerExchangeFilterFunction lbFunction
    ) {
        this.threadPoolSize = threadPoolSize;
        this.taskQueueSize = taskQueueSize;
        this.lbFunction = lbFunction;
    }

    @Bean
    public Scheduler publishEventScheduler() {
        LOG.info("Creates a messagingScheduler with connectionPoolSize = {}", threadPoolSize);
        return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "publish-pool");
    }

    @Bean   // turn all the webclient to load balanced when sending, if there's more than one.
    @Profile("eureka")
    public WebClient loadBalancedWebClient(WebClient.Builder builder) {
        return builder.filter(lbFunction).build();
    }

    @Bean   // it doesn't work with Kubernetes
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
