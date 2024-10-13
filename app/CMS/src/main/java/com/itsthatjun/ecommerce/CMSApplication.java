package com.itsthatjun.ecommerce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class CMSApplication {

    private static final Logger LOG = LoggerFactory.getLogger(CMSApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(CMSApplication.class, args);

        String postgresSqlURL = context.getEnvironment().getProperty("spring.r2dbc.url");
        LOG.info("Connected to Postgres:" + postgresSqlURL);
    }
}
