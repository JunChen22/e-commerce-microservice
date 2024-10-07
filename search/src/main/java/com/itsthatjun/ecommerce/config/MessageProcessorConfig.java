package com.itsthatjun.ecommerce.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    /*  create, update, and delete search product from PMS
    @Bean
    public Consumer<CmsAdminArticleEvent> articleMessageProcessor() {
        // lambda expression of override method accept
        return event -> {

            }
        };
    }
     */
}
