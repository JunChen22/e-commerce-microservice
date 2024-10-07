package com.itsthatjun.ecommerce.dao;

import com.itsthatjun.ecommerce.dto.admin.AdminArticleInfo;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminArticleDao {

    @ApiModelProperty(value = "get all articles and its media content")
    List<AdminArticleInfo> listAllArticles();

    @ApiModelProperty(value = "get one article and its media content")
    AdminArticleInfo getArticle(@Param("id") int id);
}
