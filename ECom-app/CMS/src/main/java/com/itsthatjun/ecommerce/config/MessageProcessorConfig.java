package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.ArticleInfo;
import com.itsthatjun.ecommerce.dto.event.CmsAdminArticleEvent;
import com.itsthatjun.ecommerce.mbg.model.Article;
import com.itsthatjun.ecommerce.service.impl.ArticleServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final ArticleServiceImpl articleService;

    @Autowired
    public MessageProcessorConfig(ArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    @Bean
    public Consumer<CmsAdminArticleEvent> articleMessageProcessor() {
        // lambda expression of override method accept
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            ArticleInfo articleInfo = event.getArticleInfo();
            switch (event.getEventType()) {
                case CREATE:
                    articleService.createArticle(articleInfo).block();
                    break;

                case UPDATE:
                    articleService.updateArticle(articleInfo).block();
                    break;

                case DELETE:
                    int articleId = event.getArticleID();
                    articleService.deleteArticle(articleId).block();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected CREATE, " +
                            "UPDATE, and DELETE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage); // TODO: create event exception
            }
        };
    }
}
