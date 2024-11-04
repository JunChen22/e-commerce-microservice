package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ReviewUpdateLog;
import com.itsthatjun.ecommerce.mbg.model.ReviewUpdateLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ReviewUpdateLogMapper {
    long countByExample(ReviewUpdateLogExample example);

    int deleteByExample(ReviewUpdateLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ReviewUpdateLog row);

    int insertSelective(ReviewUpdateLog row);

    List<ReviewUpdateLog> selectByExample(ReviewUpdateLogExample example);

    ReviewUpdateLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ReviewUpdateLog row, @Param("example") ReviewUpdateLogExample example);

    int updateByExample(@Param("row") ReviewUpdateLog row, @Param("example") ReviewUpdateLogExample example);

    int updateByPrimaryKeySelective(ReviewUpdateLog row);

    int updateByPrimaryKey(ReviewUpdateLog row);
}