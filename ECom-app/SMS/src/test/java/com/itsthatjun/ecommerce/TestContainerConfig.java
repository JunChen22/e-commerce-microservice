package com.itsthatjun.ecommerce;

import org.junit.jupiter.api.AfterAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

public abstract class TestContainerConfig {

    private static final String INIT_SCRIPT_PATH = "data.sql";
    private static final DockerImageName postgresImageName = DockerImageName.parse("postgres:9.6.10");
    private static final DockerImageName rabbitImageName = DockerImageName.parse("rabbitmq:3.8.11-management");

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(postgresImageName).withInitScript(INIT_SCRIPT_PATH);

    private static final RabbitMQContainer rabbitMQ = new RabbitMQContainer(rabbitImageName);

    @AfterAll
    static void afterAllBase() {
        postgres.stop();
        rabbitMQ.stop();
    }

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(postgres, rabbitMQ).join();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQ::getAdminPassword);
    }
}
