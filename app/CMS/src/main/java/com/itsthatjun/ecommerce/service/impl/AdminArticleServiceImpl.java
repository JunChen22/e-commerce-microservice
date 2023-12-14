package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dao.AdminArticleDao;
import com.itsthatjun.ecommerce.dto.DTOMapper;
import com.itsthatjun.ecommerce.dto.admin.AdminArticleInfo;
import com.itsthatjun.ecommerce.exceptions.ArticleException;
import com.itsthatjun.ecommerce.mbg.mapper.*;
import com.itsthatjun.ecommerce.mbg.model.*;
import com.itsthatjun.ecommerce.service.AdminArticleService;
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
public class AdminArticleServiceImpl implements AdminArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleMapper articleMapper;

    private final ArticleVideoMapper videoMapper;

    private final ArticleQaMapper qaMapper;

    private final ArticleImageMapper imageMapper;

    private final AdminArticleDao articleDao;

    private final ArticleChangeLogMapper logMapper;

    private final Scheduler jdbcScheduler;

    private final Random randomNumberGenerator = new Random();

    private final DTOMapper dtoMapper;

    @Autowired
    public AdminArticleServiceImpl(ArticleMapper articleMapper, ArticleVideoMapper videoMapper, ArticleQaMapper qaMapper,
                              ArticleImageMapper imageMapper, ArticleChangeLogMapper logMapper, AdminArticleDao articleDao,
                              @Qualifier("jdbcScheduler") Scheduler jdbcScheduler, DTOMapper dtoMapper) {
        this.articleMapper = articleMapper;
        this.videoMapper = videoMapper;
        this.qaMapper = qaMapper;
        this.imageMapper = imageMapper;
        this.logMapper = logMapper;
        this.articleDao = articleDao;
        this.jdbcScheduler = jdbcScheduler;
        this.dtoMapper = dtoMapper;
    }

    @Override
    public Flux<AdminArticleInfo> getAllArticles() {
        return Mono.fromCallable(() -> {
            List<AdminArticleInfo> articleInfoList = articleDao.getAllArticles();
            return articleInfoList;
        }).flatMapMany(Flux::fromIterable).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<AdminArticleInfo> getArticle(int articleId, int delay, int faultPercent) {
        return Mono.fromCallable(() -> {
            AdminArticleInfo article = articleDao.getArticle(articleId);
            return article;
        }).map(e -> throwErrorIfBadLuck(e, faultPercent)).delayElement(Duration.ofSeconds(delay)).subscribeOn(jdbcScheduler);
    }

    /*
        use to test/simulate circuit breaker. fault percent.
    */
    private AdminArticleInfo throwErrorIfBadLuck(AdminArticleInfo articleInfo, int faultPercent) {
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
    public Mono<AdminArticleInfo> createArticle(AdminArticleInfo articleInfo, String operator) {
        return Mono.fromCallable(() ->
                // Offload the blocking operation to the specified scheduler
                internalCreateArticle(articleInfo, operator)
        ).subscribeOn(jdbcScheduler);
    }

    private AdminArticleInfo internalCreateArticle(AdminArticleInfo articleInfo, String operator) {
        Article article = dtoMapper.adminArticleInfoToArticle(articleInfo);
        Date currentDate = new Date();
        article.setCreatedAt(currentDate);
        article.setUpdatedAt(currentDate);

        articleMapper.insert(article);

        int articleId = article.getId();

        for (ArticleQa qa : articleInfo.getQA()) {
            qa.setArticleId(articleId);
            qa.setCreatedAt(currentDate);
            qa.setUpdatedAt(currentDate);
            qaMapper.insert(qa);
        }

        for (ArticleImage image : articleInfo.getImages()) {
            image.setArticleId(articleId);
            image.setCreatedAt(currentDate);
            image.setUpdatedAt(currentDate);
            imageMapper.insert(image);
        }

        for (ArticleVideo video : articleInfo.getVideos()) {
            video.setArticleId(articleId);
            video.setCreatedAt(currentDate);
            video.setUpdatedAt(currentDate);
            videoMapper.insert(video);
        }

        articleLog(articleId, "new article", operator);
        return articleInfo;
    }

    @Override    // TODO: update existing one, even with deletion
    public Mono<AdminArticleInfo> updateArticle(AdminArticleInfo articleInfo, String operator) {
        return Mono.fromCallable(() -> {
            // Offload the blocking operation to the specified scheduler
            int articleId = articleInfo.getId();

            Article article = dtoMapper.adminArticleInfoToArticle(articleInfo);
            article.setUpdatedAt(new Date());
            articleMapper.updateByPrimaryKeySelective(article);

            updateQA(articleId, articleInfo.getQA());
            updateImage(articleId, articleInfo.getImages());
            updateVideo(articleId, articleInfo.getVideos());

            articleLog(articleId, "update article", operator);
            return articleInfo;
        }).subscribeOn(jdbcScheduler);
    }

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

            articleMapper.deleteByPrimaryKey(articleId);

            ArticleQaExample articleQaExample= new ArticleQaExample();
            articleQaExample.createCriteria().andArticleIdEqualTo(articleId);
            List<ArticleQa> qaList = qaMapper.selectByExample(articleQaExample);
            for (ArticleQa qa : qaList) {
                qaMapper.deleteByPrimaryKey(qa.getId());
            }

            ArticleVideoExample articleVideoExample = new ArticleVideoExample();
            articleVideoExample.createCriteria().andArticleIdEqualTo(articleId);
            List<ArticleVideo> videoList = videoMapper.selectByExample(articleVideoExample);
            for (ArticleVideo video : videoList) {
                videoMapper.deleteByPrimaryKey(video.getId());
            }

            ArticleImageExample articleImageExample = new ArticleImageExample();
            articleImageExample.createCriteria().andArticleIdEqualTo(articleId);
            List<ArticleImage> imageList = imageMapper.selectByExample(articleImageExample);
            for (ArticleImage image : imageList) {
                imageMapper.deleteByPrimaryKey(image.getId());
            }
            articleLog(articleId, "delete article", operator);
        }).subscribeOn(jdbcScheduler).then();
    }

    private void articleLog(int articleId, String updateAction, String operator) {
        ArticleChangeLog changeLog = new ArticleChangeLog();
        changeLog.setArticleId(articleId);
        changeLog.setUpdateAction(updateAction);
        changeLog.setOperator(operator);
        changeLog.setCreatedAt(new Date());
        logMapper.insert(changeLog);
    }
}
