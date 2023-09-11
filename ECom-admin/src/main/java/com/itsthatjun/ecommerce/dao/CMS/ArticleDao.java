package com.itsthatjun.ecommerce.dao.CMS;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ArticleDao {

    @ApiModelProperty(value = "get all articles and its media content")
    List<ArticleInfo> getAllArticles();

    @ApiModelProperty(value = "get one article and its media content")
    ArticleInfo getArticle(@Param("id") int id);

}
