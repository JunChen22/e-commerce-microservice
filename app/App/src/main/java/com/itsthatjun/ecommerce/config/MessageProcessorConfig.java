package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.event.cms.ArticleUpdateEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    @Bean
    public Consumer<ArticleUpdateEvent> cmsArticleMessageProcessor() {
        return event -> {
            switch (event.getEventType()) {
                // TODO: Implement the logic to process the incoming event
                //     update the article in redis cache
                case CREATE:
                    System.out.println("Create article: " + event.getArticleInfo());
                    break;

                case UPDATE:
                    System.out.println("Update article: " + event.getArticleInfo());
                    break;

                case DELETE:
                    System.out.println("Delete article: " + event.getArticleInfo());
                    break;

                default:
                    throw new RuntimeException("Incorrect event type: " + event.getEventType());
            }
        };
    }
}
