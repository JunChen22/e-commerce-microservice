package com.itsthatjun.ecommerce.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue cancelOrderTTL() {
        return QueueBuilder.durable("orderCancelQueue.orderGroup")
                .withArgument("x-message-ttl", 120000) // 120 seconds
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "orderComplete.orderGroup")
                .build();
    }
}
