package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dao.ArticleDao;
import com.itsthatjun.ecommerce.dto.Articles;
import com.itsthatjun.ecommerce.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleDao articleDao;

    @Autowired
    public ArticleServiceImpl(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }

    @Override
    public List<Articles> getAllArticles() {
        //  TODO: it works but too much query to database
        return articleDao.getAllArticles();
    }

    @Override
    public Articles getArticle(int articleId) {
        return articleDao.getArticle(articleId);
    }
}
