package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ArticleImage;
import com.itsthatjun.ecommerce.mbg.model.ArticleImageExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ArticleImageMapper {
    long countByExample(ArticleImageExample example);

    int deleteByExample(ArticleImageExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ArticleImage row);

    int insertSelective(ArticleImage row);

    List<ArticleImage> selectByExample(ArticleImageExample example);

    ArticleImage selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ArticleImage row, @Param("example") ArticleImageExample example);

    int updateByExample(@Param("row") ArticleImage row, @Param("example") ArticleImageExample example);

    int updateByPrimaryKeySelective(ArticleImage row);

    int updateByPrimaryKey(ArticleImage row);
}