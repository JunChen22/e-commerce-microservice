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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleMapper articleMapper;

    private final ArticleVideoMapper videoMapper;

    private final ArticleQaMapper qaMapper;

    private final ArticleImageMapper imageMapper;

    private final ArticleDao articleDao;

    @Autowired
    public ArticleServiceImpl(ArticleMapper articleMapper, ArticleVideoMapper videoMapper, ArticleQaMapper qaMapper,
                              ArticleImageMapper imageMapper, ArticleDao articleDao) {
        this.articleMapper = articleMapper;
        this.videoMapper = videoMapper;
        this.qaMapper = qaMapper;
        this.imageMapper = imageMapper;
        this.articleDao = articleDao;
    }

    @Override
    public Flux<ArticleInfo> getAllArticles() {
        //  TODO: it works but too much query to database
        List<ArticleInfo> articleInfoList =  articleDao.getAllArticles();
        return Flux.fromIterable(articleInfoList);
    }

    @Override
    public Mono<ArticleInfo> getArticle(int articleId) {
        ArticleInfo articleInfo = articleDao.getArticle(articleId);
        if (articleInfo != null) {
            return Mono.just(articleInfo);
        } else {
            return Mono.error(new ArticleException("Article not found"));
        }
    }

    @Override
    public void createArticle(ArticleInfo article) {
        //TODO: optimize this
        Article exist = articleMapper.selectByPrimaryKey(article.getArticle().getId());
        if(exist != null) {
            System.out.println("existing id");
            // TODO: return exception tobe added in ECom-common
            return;
        }

        Date currentDate = new Date();
        article.getArticle().setCreatedAt(currentDate);
        articleMapper.insert(article.getArticle());
        int articleID = article.getArticle().getId();
        if(!article.getQA().isEmpty()){
            for(ArticleQa qa: article.getQA()) {
                qa.setArticleId(articleID);
                qa.setCreatedAt(currentDate);
                qaMapper.insert(qa);
            }
        }

        if(!article.getImages().isEmpty()){
            for(ArticleImage image: article.getImages()) {
                image.setArticleId(articleID);
                image.setCreatedAt(currentDate);
                imageMapper.insert(image);
            }
        }

        if(!article.getVideos().isEmpty()){
            for(ArticleVideo video: article.getVideos()) {
                video.setArticleId(articleID);
                video.setCreatedAt(currentDate);
                videoMapper.insert(video);
            }
        }
    }

    @Override
    // TODO: update when deleting one of the element
    public void updateArticle(ArticleInfo articleInfo) {
        int articleId = articleInfo.getArticle().getId();

        Article article = articleInfo.getArticle();
        article.setUpdatedAt(new Date());

        articleMapper.updateByPrimaryKey(article);
        updateQA(articleId, articleInfo.getQA());
        updateImage(articleId, articleInfo.getImages());
        updateVideo(articleId, articleInfo.getVideos());
    }

    // TODO: newly created won't have primary key, only update existing pic not newly added
    private int updateQA(int articleId, List<ArticleQa> qaList) {
        for (ArticleQa qa: qaList) {
            qa.setUpdatedAt(new Date());
            qa.setArticleId(articleId);
            qaMapper.updateByPrimaryKey(qa);
        }
        return qaList.size();
    }

    private int updateImage(int articleId, List<ArticleImage> imageList){
        for (ArticleImage image: imageList) {
            image.setUpdatedAt(new Date());
            image.setArticleId(articleId);
            imageMapper.updateByPrimaryKey(image);
        }
        return imageList.size();
    }

    private int updateVideo(int articleId, List<ArticleVideo> videoList) {
        for (ArticleVideo video: videoList) {
            video.setUpdatedAt(new Date());
            video.setArticleId(articleId);
            videoMapper.updateByPrimaryKey(video);
        }
        return videoList.size();
    }

    @Override
    public void deleteArticle(int articleId) {

        if (articleMapper.selectByPrimaryKey(articleId) == null) {
            throw new ArticleException("Article not found with ID: " + articleId);
        }

        ArticleInfo article = articleDao.getArticle(articleId);

        articleMapper.deleteByPrimaryKey(articleId);

        for (ArticleQa qa: article.getQA()) {
            qaMapper.deleteByPrimaryKey(qa.getId());
        }

        for (ArticleVideo video: article.getVideos()) {
            videoMapper.deleteByPrimaryKey(video.getId());
        }

        for (ArticleImage image: article.getImages()) {
            imageMapper.deleteByPrimaryKey(image.getId());
        }
    }
}
