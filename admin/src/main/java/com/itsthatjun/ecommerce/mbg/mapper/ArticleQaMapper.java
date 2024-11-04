package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ArticleQa;
import com.itsthatjun.ecommerce.mbg.model.ArticleQaExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ArticleQaMapper {
    long countByExample(ArticleQaExample example);

    int deleteByExample(ArticleQaExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ArticleQa row);

    int insertSelective(ArticleQa row);

    List<ArticleQa> selectByExample(ArticleQaExample example);

    ArticleQa selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ArticleQa row, @Param("example") ArticleQaExample example);

    int updateByExample(@Param("row") ArticleQa row, @Param("example") ArticleQaExample example);

    int updateByPrimaryKeySelective(ArticleQa row);

    int updateByPrimaryKey(ArticleQa row);
}