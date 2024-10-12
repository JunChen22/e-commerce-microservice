package com.itsthatjun.ecommerce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class CMSApplication {

    private static final Logger LOG = LoggerFactory.getLogger(CMSApplication.class);

    private final Integer threadPoolSize;

    private final Integer taskQueueSize;

    @Autowired
    public CMSApplication(
            @Value("${app.threadPoolSize:2}") Integer threadPoolSize,
            @Value("${app.taskQueueSize:100}") Integer taskQueueSize
    ) {
        this.threadPoolSize = threadPoolSize;
        this.taskQueueSize = taskQueueSize;
    }

    @Bean
    public Scheduler publishEventScheduler() {
        LOG.info("Creates a messagingScheduler with connectionPoolSize = {}", threadPoolSize);
        return Schedulers.newBoundedElastic(threadPoolSize, taskQueueSize, "publish-pool");
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CMSApplication.class, args);

        String postgresSqlURL = context.getEnvironment().getProperty("spring.r2dbc.url");
        LOG.info("Connected to Postgres:" + postgresSqlURL);
    }
}
