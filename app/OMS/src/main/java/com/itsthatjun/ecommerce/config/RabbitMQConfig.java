package com.itsthatjun.ecommerce.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${custom.rabbitMQ.orderCancelTTL-time}")
    private int orderCancelTTLTime;

    @Value("${custom.rabbitMQ.returnRequestTTL-time}")
    private int returnRequestTTLTime;

    @Bean
    public Queue orderCancelQueue() {
        return QueueBuilder.durable("orderCancelQueue.orderGroup")
                .withArgument("x-message-ttl", orderCancelTTLTime) //
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "orderComplete.orderGroup")
                .build();
    }

    @Bean
    public Queue returnRequestQueue() {
        return QueueBuilder.durable("returnRequestQueue.returnGroup")
                .withArgument("x-message-ttl", returnRequestTTLTime) // 3 days
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", "return.returnGroup")
                .build();
    }
}
