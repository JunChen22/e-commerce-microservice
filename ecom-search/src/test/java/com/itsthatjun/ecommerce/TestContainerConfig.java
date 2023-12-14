package com.itsthatjun.ecommerce;

import org.junit.jupiter.api.AfterAll;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

@EnableElasticsearchRepositories(basePackages = "com.itsthatjun.ecommerce.elasticsearch.repository")
public abstract class TestContainerConfig {

    private static final String INIT_SCRIPT_PATH = "data.sql";
    private static final DockerImageName postgresImageName = DockerImageName.parse("postgres:9.6.10");
    private static final DockerImageName mongoImageName = DockerImageName.parse("mongo:5.0.0");
    private static final DockerImageName elasticSearchImageName = DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.12.0");

    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(postgresImageName).withInitScript(INIT_SCRIPT_PATH);

    private static final MongoDBContainer mongo = new MongoDBContainer(mongoImageName);

    private static final ElasticsearchContainer elasticsearch = new ElasticsearchContainer(elasticSearchImageName);

    @AfterAll
    static void afterAllBase() {
        postgres.stop();
        mongo.stop();
        elasticsearch.stop();
    }

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(postgres, mongo, elasticsearch).join();
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);

        String mongoHost = mongo.getHost();
        Integer mongoMappedPort = mongo.getMappedPort(27017);

        registry.add("spring.data.mongodb.host", () -> mongoHost);
        registry.add("spring.data.mongodb.port", () -> mongoMappedPort);

        String elasticsearchHost = elasticsearch.getHost();
        Integer elasticsearchPort = elasticsearch.getFirstMappedPort();

        registry.add("spring.elasticsearch.rest.uris" , () -> "http://" + elasticsearchHost + ":" + elasticsearchPort);
    }
}
