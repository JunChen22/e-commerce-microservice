package com.itsthatjun.ecommerce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class NotificationApplication {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationApplication.class);

    private final Integer threadPoolSize;

    private final Integer taskQueueSize;

    @Autowired
    public NotificationApplication(
            @Value("${app.threadPoolSize:2}") Integer threadPoolSize,
            @Value("${app.taskQueueSize:100}") Integer taskQueueSize
    ) {
        this.threadPoolSize = threadPoolSize;
        this.taskQueueSize = taskQueueSize;
    }

    public static void main(String[] args) {
        SpringApplication.run(NotificationApplication.class, args);
    }

    @Bean
    public Scheduler emailSenderScheduler() {
        LOG.info("Creates a emailSenderScheduler with connectionPoolSize = {}", threadPoolSize);
        return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "publish-pool");
    }

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }
}