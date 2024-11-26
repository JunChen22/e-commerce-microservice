package com.itsthatjun.ecommerce;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UMSApplication {

    private static final Logger LOG = LoggerFactory.getLogger(UMSApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(UMSApplication.class, args);

        String postgresSqlURL = context.getEnvironment().getProperty("spring.r2dbc.url");
        LOG.info("Connected to Postgres: {}", postgresSqlURL);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
