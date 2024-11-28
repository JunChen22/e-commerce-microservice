package com.itsthatjun.ecommerce.service.eventupdate;

import com.itsthatjun.ecommerce.repository.ArticleAnalyticRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArticleAnalyticUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleAnalyticUpdateService.class);

    private final ArticleAnalyticRepository analyticRepository;

    @Autowired
    public ArticleAnalyticUpdateService(ArticleAnalyticRepository analyticRepository) {
        this.analyticRepository = analyticRepository;
    }

    // TODO: Implement this method, and add dto/event for analytic update
//    public Mono<Void> updateArticleAnalytic(int articleId) {
//        LOG.info("Updating article analytic for article id: {}", articleId);
//    }
}
