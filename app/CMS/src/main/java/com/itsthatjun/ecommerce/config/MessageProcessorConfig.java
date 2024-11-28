package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.event.incoming.ArticleAnalyticEvent;
import com.itsthatjun.ecommerce.service.eventupdate.ArticleAnalyticUpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final ArticleAnalyticUpdateService analyticUpdateService;

    @Autowired
    public MessageProcessorConfig(ArticleAnalyticUpdateService analyticUpdateService) {
        this.analyticUpdateService = analyticUpdateService;
    }

    @Bean
    public Consumer<ArticleAnalyticEvent> articleAnalyticMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Processing article analytic event: {}", event);
            //analyticUpdateService.updateArticleAnalytic(event.getArticleId(), event.getHour(), event.getViewCount(), event.getLikeCount(), event.getShareCount(), event.getCommentCount());
        };
    }
}
