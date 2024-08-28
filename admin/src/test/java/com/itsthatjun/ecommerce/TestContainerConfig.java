package com.itsthatjun.ecommerce;

import org.junit.jupiter.api.AfterAll;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

public abstract class TestContainerConfig {

    private static final String INIT_SCRIPT_PATH = "data.sql";
    private static final DockerImageName postgresImageName = DockerImageName.parse("postgres:16-bullseye");
    private static final DockerImageName redisImageName = DockerImageName.parse("redis:7.0.14");
    private static final DockerImageName mongoImageName = DockerImageName.parse("mongo:5.0.0");
    private static final DockerImageName rabbitImageName = DockerImageName.parse("rabbitmq:3.8.11-management");

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(postgresImageName).withInitScript(INIT_SCRIPT_PATH);

    static final GenericContainer<?> redis = new GenericContainer<>(redisImageName).withExposedPorts(6379);

    private static final MongoDBContainer mongo = new MongoDBContainer(mongoImageName);

    private static final RabbitMQContainer rabbitMQ = new RabbitMQContainer(rabbitImageName);

    @AfterAll
    static void afterAllBase() {
        postgres.stop();
        redis.stop();
        mongo.stop();
        rabbitMQ.stop();
    }

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(postgres, mongo, redis, rabbitMQ).join();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);

        String mongoHost = mongo.getHost();
        Integer mongoMappedPort = mongo.getMappedPort(27017);

        registry.add("spring.data.mongodb.host", () -> mongoHost);
        registry.add("spring.data.mongodb.port", () -> mongoMappedPort);

        registry.add("spring.rabbitmq.host", rabbitMQ::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQ::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitMQ::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQ::getAdminPassword);
    }
}
