package com.itsthatjun.ecommerce.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import java.time.Duration;

@Configuration
public class ElasticsearchClientConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticsearchUri;

    @Value("${spring.elasticsearch.connection-timeout}")
    private String connectionTimeout;

    @Value("${spring.elasticsearch.socket-timeout}")
    private String socketTimeout;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUri.replace("http://", ""))
                .withConnectTimeout(Duration.parse("PT" + connectionTimeout))
                .withSocketTimeout(Duration.parse("PT" + socketTimeout))
                .build();
    }
}