package com.itsthatjun.ecommerce.dao;

import com.itsthatjun.ecommerce.dto.ArticleInfo;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArticleDao {

    @ApiModelProperty(value = "list all articles and its media content")
    List<ArticleInfo> listAllArticles();

    @ApiModelProperty(value = "get one article and its media content")
    ArticleInfo getArticle(@Param("id") int id);
}
