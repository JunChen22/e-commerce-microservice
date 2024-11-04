package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.Review;
import com.itsthatjun.ecommerce.mbg.model.ReviewExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ReviewMapper {
    long countByExample(ReviewExample example);

    int deleteByExample(ReviewExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Review row);

    int insertSelective(Review row);

    List<Review> selectByExample(ReviewExample example);

    Review selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") Review row, @Param("example") ReviewExample example);

    int updateByExample(@Param("row") Review row, @Param("example") ReviewExample example);

    int updateByPrimaryKeySelective(Review row);

    int updateByPrimaryKey(Review row);
}