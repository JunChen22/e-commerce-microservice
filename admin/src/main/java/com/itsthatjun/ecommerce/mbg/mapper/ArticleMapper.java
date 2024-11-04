package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.Article;
import com.itsthatjun.ecommerce.mbg.model.ArticleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ArticleMapper {
    long countByExample(ArticleExample example);

    int deleteByExample(ArticleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Article row);

    int insertSelective(Article row);

    List<Article> selectByExample(ArticleExample example);

    Article selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") Article row, @Param("example") ArticleExample example);

    int updateByExample(@Param("row") Article row, @Param("example") ArticleExample example);

    int updateByPrimaryKeySelective(Article row);

    int updateByPrimaryKey(Article row);
}