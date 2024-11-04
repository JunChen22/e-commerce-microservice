package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ArticleChangeLog;
import com.itsthatjun.ecommerce.mbg.model.ArticleChangeLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ArticleChangeLogMapper {
    long countByExample(ArticleChangeLogExample example);

    int deleteByExample(ArticleChangeLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ArticleChangeLog row);

    int insertSelective(ArticleChangeLog row);

    List<ArticleChangeLog> selectByExample(ArticleChangeLogExample example);

    ArticleChangeLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ArticleChangeLog row, @Param("example") ArticleChangeLogExample example);

    int updateByExample(@Param("row") ArticleChangeLog row, @Param("example") ArticleChangeLogExample example);

    int updateByPrimaryKeySelective(ArticleChangeLog row);

    int updateByPrimaryKey(ArticleChangeLog row);
}