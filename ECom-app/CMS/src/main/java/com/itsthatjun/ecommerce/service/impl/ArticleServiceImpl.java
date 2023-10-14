package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dao.ArticleDao;
import com.itsthatjun.ecommerce.dto.ArticleInfo;
import com.itsthatjun.ecommerce.exceptions.ArticleException;
import com.itsthatjun.ecommerce.mbg.mapper.ArticleImageMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ArticleMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ArticleQaMapper;
import com.itsthatjun.ecommerce.mbg.mapper.ArticleVideoMapper;
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

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleMapper articleMapper;

    private final ArticleVideoMapper videoMapper;

    private final ArticleQaMapper qaMapper;

    private final ArticleImageMapper imageMapper;

    private final ArticleDao articleDao;

    private final Scheduler jdbcScheduler;

    @Autowired
    public ArticleServiceImpl(ArticleMapper articleMapper, ArticleVideoMapper videoMapper, ArticleQaMapper qaMapper,
                              ArticleImageMapper imageMapper, ArticleDao articleDao,
                              @Qualifier("jdbcScheduler") Scheduler jdbcScheduler) {
        this.articleMapper = articleMapper;
        this.videoMapper = videoMapper;
        this.qaMapper = qaMapper;
        this.imageMapper = imageMapper;
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
    public Mono<ArticleInfo> getArticle(int articleId) {
        return Mono.fromCallable(() ->
                articleDao.getArticle(articleId)
        ).subscribeOn(jdbcScheduler);
    }

    @Override
    public Mono<ArticleInfo> createArticle(ArticleInfo articleInfo) {
        return Mono.fromCallable(() ->
            // Offload the blocking operation to the specified scheduler
            internalCreateArticle(articleInfo)
        ).subscribeOn(jdbcScheduler);
    }

    private ArticleInfo internalCreateArticle(ArticleInfo articleInfo) {
        //TODO: optimize this
        Article exist = articleMapper.selectByPrimaryKey(articleInfo.getArticle().getId());
        if (exist != null) {
            // TODO: return exception tobe added in ECom-common
            throw new RuntimeException("Article id already exist:" + exist.getId());
        }

        Date currentDate = new Date();
        articleInfo.getArticle().setCreatedAt(currentDate);
        articleMapper.insert(articleInfo.getArticle());
        int articleID = articleInfo.getArticle().getId();
        if (!articleInfo.getQA().isEmpty()) {
            for (ArticleQa qa : articleInfo.getQA()) {
                qa.setArticleId(articleID);
                qa.setCreatedAt(currentDate);
                qaMapper.insert(qa);
            }
        }

        if (!articleInfo.getImages().isEmpty()) {
            for (ArticleImage image : articleInfo.getImages()) {
                image.setArticleId(articleID);
                image.setCreatedAt(currentDate);
                imageMapper.insert(image);
            }
        }

        if (!articleInfo.getVideos().isEmpty()) {
            for (ArticleVideo video : articleInfo.getVideos()) {
                video.setArticleId(articleID);
                video.setCreatedAt(currentDate);
                videoMapper.insert(video);
            }
        }
        return articleInfo;
    }

    @Override
    public Mono<ArticleInfo> updateArticle(ArticleInfo articleInfo) {
        return Mono.fromCallable(() ->
            // Offload the blocking operation to the specified scheduler
            internalUpdateArticle(articleInfo)
        ).subscribeOn(jdbcScheduler);
    }

    // TODO: update when deleting one of the element
    private ArticleInfo internalUpdateArticle(ArticleInfo articleInfo) {
        int articleId = articleInfo.getArticle().getId();

        Article article = articleInfo.getArticle();
        article.setUpdatedAt(new Date());

        articleMapper.updateByPrimaryKey(article);
        updateQA(articleId, articleInfo.getQA());
        updateImage(articleId, articleInfo.getImages());
        updateVideo(articleId, articleInfo.getVideos());

        return articleInfo;
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
    public Mono<Void> deleteArticle(int articleId) {
        return Mono.fromRunnable(() ->
                internalDeleteArticle(articleId)
        ).subscribeOn(jdbcScheduler).then();
    }

    public void internalDeleteArticle(int articleId) {
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
    }
}
