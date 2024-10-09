package com.itsthatjun.ecommerce.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.core.DatabaseClient;

//@Configuration
//@EnableR2dbcRepositories
public class R2dbcConfig  {
//public class R2dbcConfig extends AbstractR2dbcConfiguration {
//
//    @Bean
//    @Override
//    public ConnectionFactory connectionFactory() {
//        return ConnectionFactories.get("r2dbc:postgresql://email-db:5439/emaildb");
//    }

//    @Bean
//    public DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
//        return DatabaseClient.builder()
//                .connectionFactory(connectionFactory)
//                .build();
//    }
}
