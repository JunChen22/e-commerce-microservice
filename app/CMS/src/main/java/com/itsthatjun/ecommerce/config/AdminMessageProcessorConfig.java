package com.itsthatjun.ecommerce.config;

import com.itsthatjun.ecommerce.dto.admin.AdminArticleInfo;
import com.itsthatjun.ecommerce.dto.event.incoming.CmsAdminArticleEvent;
import com.itsthatjun.ecommerce.service.impl.AdminArticleServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
public class MessageProcessorConfig {

    private static final Logger LOG = LoggerFactory.getLogger(MessageProcessorConfig.class);

    private final AdminArticleServiceImpl articleService;

    @Autowired
    public MessageProcessorConfig(AdminArticleServiceImpl articleService) {
        this.articleService = articleService;
    }

    @Bean
    public Consumer<CmsAdminArticleEvent> articleMessageProcessor() {
        return event -> {
            LOG.info("Process message created at {}...", event.getEventCreatedAt());
            AdminArticleInfo articleInfo = event.getArticleInfo();
            String operator = event.getOperator();

            switch (event.getEventType()) {
                case CREATE:
                    articleService.createArticle(articleInfo, operator).subscribe();
                    break;

                case UPDATE:
                    articleService.updateArticle(articleInfo, operator).subscribe();
                    break;

                case DELETE:
                    int articleId = articleInfo.getArticle().getId();
                    articleService.deleteArticle(articleId, operator).subscribe();
                    break;

                default:
                    String errorMessage = "Incorrect event type:" + event.getEventType() + ", expected CREATE, " +
                            "UPDATE, and DELETE event";
                    LOG.warn(errorMessage);
                    throw new RuntimeException(errorMessage);
            }
        };
    }
}
