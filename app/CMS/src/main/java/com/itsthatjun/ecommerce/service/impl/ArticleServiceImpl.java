package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dao.ArticleDao;
import com.itsthatjun.ecommerce.dto.ArticleInfo;
import com.itsthatjun.ecommerce.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import java.time.Duration;
import java.util.Random;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleDao articleDao;

    private final Scheduler jdbcScheduler;

    private final Random randomNumberGenerator = new Random();

    @Autowired
    public ArticleServiceImpl(ArticleDao articleDao, @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.articleDao = articleDao;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Flux<ArticleInfo> listAllArticles() {
        return Mono.fromCallable(() ->
                articleDao.listAllArticles()
        ).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<ArticleInfo> getArticle(int articleId, int delay, int faultPercent) {
        return Mono.fromCallable(() ->
                articleDao.getArticle(articleId)
        ).map(e -> throwErrorIfBadLuck(e, faultPercent)).delayElement(Duration.ofSeconds(delay)).subscribeOn(jdbcScheduler);
    }

    /*
        use to test/simulate circuit breaker. fault percent.
    */
    private ArticleInfo throwErrorIfBadLuck(ArticleInfo articleInfo, int faultPercent) {
        if (faultPercent == 0) return articleInfo;

        int randomThreshold = randomNumberGenerator.nextInt(100);

        if (faultPercent < randomThreshold) {
            LOG.debug("We got lucky, no error occurred, {} < {}", faultPercent, randomThreshold);
        } else {
            LOG.debug("Bad luck, an error occurred, {} >= {}", faultPercent, randomThreshold);
            throw new RuntimeException("Something went wrong...");
        }

        return articleInfo;
    }
}
