package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ArticleVideo;
import com.itsthatjun.ecommerce.mbg.model.ArticleVideoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ArticleVideoMapper {
    long countByExample(ArticleVideoExample example);

    int deleteByExample(ArticleVideoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ArticleVideo row);

    int insertSelective(ArticleVideo row);

    List<ArticleVideo> selectByExample(ArticleVideoExample example);

    ArticleVideo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ArticleVideo row, @Param("example") ArticleVideoExample example);

    int updateByExample(@Param("row") ArticleVideo row, @Param("example") ArticleVideoExample example);

    int updateByPrimaryKeySelective(ArticleVideo row);

    int updateByPrimaryKey(ArticleVideo row);
}