package com.itsthatjun.ecommerce;

import org.junit.jupiter.api.AfterAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

public abstract class TestContainerConfig {

    private static final String INIT_SCRIPT_PATH = "data_test_copy.sql";
    private static final DockerImageName postgresImageName = DockerImageName.parse("postgres:16-bullseye");
    private static final DockerImageName redisImageName = DockerImageName.parse("redis:7.0.14");
    private static final DockerImageName rabbitImageName = DockerImageName.parse("rabbitmq:3.8.11-management");

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(postgresImageName).withInitScript(INIT_SCRIPT_PATH);

    static final GenericContainer<?> redis = new GenericContainer<>(redisImageName).withExposedPorts(6379);

    private static final RabbitMQContainer rabbitMQ = new RabbitMQContainer(rabbitImageName);

    @AfterAll
    static void afterAllBase() {
        postgres.stop();
        redis.stop();
        rabbitMQ.stop();
    }

    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(postgres, redis, rabbitMQ).join();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);

        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQ::getAdminPassword);
    }
}