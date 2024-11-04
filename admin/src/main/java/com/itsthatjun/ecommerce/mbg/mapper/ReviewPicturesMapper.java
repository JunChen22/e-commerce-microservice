package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ReviewPictures;
import com.itsthatjun.ecommerce.mbg.model.ReviewPicturesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ReviewPicturesMapper {
    long countByExample(ReviewPicturesExample example);

    int deleteByExample(ReviewPicturesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ReviewPictures row);

    int insertSelective(ReviewPictures row);

    List<ReviewPictures> selectByExample(ReviewPicturesExample example);

    ReviewPictures selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ReviewPictures row, @Param("example") ReviewPicturesExample example);

    int updateByExample(@Param("row") ReviewPictures row, @Param("example") ReviewPicturesExample example);

    int updateByPrimaryKeySelective(ReviewPictures row);

    int updateByPrimaryKey(ReviewPictures row);
}