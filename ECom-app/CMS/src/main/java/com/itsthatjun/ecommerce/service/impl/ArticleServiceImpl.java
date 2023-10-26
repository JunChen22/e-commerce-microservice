package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dao.ArticleDao;
import com.itsthatjun.ecommerce.dto.ArticleInfo;
import com.itsthatjun.ecommerce.exceptions.ArticleException;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
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
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleMapper articleMapper;

    private final ArticleVideoMapper videoMapper;

    private final ArticleQaMapper qaMapper;

    private final ArticleImageMapper imageMapper;

    private final ArticleDao articleDao;

    private final ArticleChangeLogMapper logMapper;

    private final Scheduler jdbcScheduler;

    private final Random randomNumberGenerator = new Random();

    @Autowired
    public ArticleServiceImpl(ArticleMapper articleMapper, ArticleVideoMapper videoMapper, ArticleQaMapper qaMapper,
                              ArticleImageMapper imageMapper, ArticleChangeLogMapper logMapper, ArticleDao articleDao,
                              @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.articleMapper = articleMapper;
        this.videoMapper = videoMapper;
        this.qaMapper = qaMapper;
        this.imageMapper = imageMapper;
        this.logMapper = logMapper;
        this.articleDao = articleDao;
        this.jdbcScheduler = jdbcScheduler;
    }

    @Override
    public Flux<ArticleInfo> getAllArticles() {
        return Mono.fromCallable(() ->
                articleDao.getAllArticles()
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

    @Override
    public Mono<ArticleInfo> createArticle(ArticleInfo articleInfo, String operator) {
        return Mono.fromCallable(() ->
            // Offload the blocking operation to the specified scheduler
            internalCreateArticle(articleInfo, operator)
        ).subscribeOn(jdbcScheduler);
    }

    private ArticleInfo internalCreateArticle(ArticleInfo articleInfo, String operator) {
        //TODO: optimize this
        Article exist = articleMapper.selectByPrimaryKey(articleInfo.getArticle().getId());
        if (exist != null) {
            // TODO: return exception to be added in ECom-common
            throw new RuntimeException("Article id already exist:" + exist.getId());
        }

        Date currentDate = new Date();
        articleInfo.getArticle().setCreatedAt(currentDate);
        articleMapper.insert(articleInfo.getArticle());
        int articleId = articleInfo.getArticle().getId();
        if (!articleInfo.getQA().isEmpty()) {
            for (ArticleQa qa : articleInfo.getQA()) {
                qa.setArticleId(articleId);
                qa.setCreatedAt(currentDate);
                qaMapper.insert(qa);
            }
        }

        if (!articleInfo.getImages().isEmpty()) {
            for (ArticleImage image : articleInfo.getImages()) {
                image.setArticleId(articleId);
                image.setCreatedAt(currentDate);
                imageMapper.insert(image);
            }
        }

        if (!articleInfo.getVideos().isEmpty()) {
            for (ArticleVideo video : articleInfo.getVideos()) {
                video.setArticleId(articleId);
                video.setCreatedAt(currentDate);
                videoMapper.insert(video);
            }
        }
        articleLog(articleId, "create article", operator);
        return articleInfo;
    }

    @Override    // TODO: update when deleting one of the element
    public Mono<ArticleInfo> updateArticle(ArticleInfo articleInfo, String operator) {
        return Mono.fromCallable(() -> {
            // Offload the blocking operation to the specified scheduler
            int articleId = articleInfo.getArticle().getId();

            Article article = articleInfo.getArticle();
            article.setUpdatedAt(new Date());

            articleMapper.updateByPrimaryKeySelective(article);
            updateQA(articleId, articleInfo.getQA());
            updateImage(articleId, articleInfo.getImages());
            updateVideo(articleId, articleInfo.getVideos());

            articleLog(articleId, "update article", operator);
            return articleInfo;
        }).subscribeOn(jdbcScheduler);
    }

    // TODO: newly created won't have primary key, only update existing pic not newly added
    private int updateQA(int articleId, List<ArticleQa> qaList) {
        for (ArticleQa qa : qaList) {
            qa.setUpdatedAt(new Date());
            qa.setArticleId(articleId);
            qaMapper.updateByPrimaryKey(qa);
        }
        return qaList.size();
    }

    private int updateImage(int articleId, List<ArticleImage> imageList) {
        for (ArticleImage image : imageList) {
            image.setUpdatedAt(new Date());
            image.setArticleId(articleId);
            imageMapper.updateByPrimaryKey(image);
        }
        return imageList.size();
    }

    private int updateVideo(int articleId, List<ArticleVideo> videoList) {
        for (ArticleVideo video : videoList) {
            video.setUpdatedAt(new Date());
            video.setArticleId(articleId);
            videoMapper.updateByPrimaryKey(video);
        }
        return videoList.size();
    }

    @Override
    public Mono<Void> deleteArticle(int articleId, String operator) {
        return Mono.fromRunnable(() -> {
            if (articleMapper.selectByPrimaryKey(articleId) == null) {
                throw new ArticleException("Article not found with ID: " + articleId);
            }

            ArticleInfo article = articleDao.getArticle(articleId);

            articleMapper.deleteByPrimaryKey(articleId);

            for (ArticleQa qa : article.getQA()) {
                qaMapper.deleteByPrimaryKey(qa.getId());
            }

            for (ArticleVideo video : article.getVideos()) {
                videoMapper.deleteByPrimaryKey(video.getId());
            }

            for (ArticleImage image : article.getImages()) {
                imageMapper.deleteByPrimaryKey(image.getId());
            }

            articleLog(articleId, "delete article", operator);
        }).subscribeOn(jdbcScheduler).then();
    }

    private void articleLog(int articleId, String updateAction, String operator) {
        ArticleChangeLog changeLog = new ArticleChangeLog();
        changeLog.setArticleId(articleId);
        changeLog.setUpdateAction(updateAction);
        changeLog.setChangeOperator(operator);
        changeLog.setCreatedAt(new Date());
        logMapper.insert(changeLog);
    }
}
