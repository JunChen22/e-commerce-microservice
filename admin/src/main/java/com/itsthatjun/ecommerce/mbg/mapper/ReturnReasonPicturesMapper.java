package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ReturnReasonPictures;
import com.itsthatjun.ecommerce.mbg.model.ReturnReasonPicturesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ReturnReasonPicturesMapper {
    long countByExample(ReturnReasonPicturesExample example);

    int deleteByExample(ReturnReasonPicturesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ReturnReasonPictures row);

    int insertSelective(ReturnReasonPictures row);

    List<ReturnReasonPictures> selectByExample(ReturnReasonPicturesExample example);

    ReturnReasonPictures selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ReturnReasonPictures row, @Param("example") ReturnReasonPicturesExample example);

    int updateByExample(@Param("row") ReturnReasonPictures row, @Param("example") ReturnReasonPicturesExample example);

    int updateByPrimaryKeySelective(ReturnReasonPictures row);

    int updateByPrimaryKey(ReturnReasonPictures row);
}