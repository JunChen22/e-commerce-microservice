package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.BrandUpdateLog;
import com.itsthatjun.ecommerce.mbg.model.BrandUpdateLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BrandUpdateLogMapper {
    long countByExample(BrandUpdateLogExample example);

    int deleteByExample(BrandUpdateLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BrandUpdateLog row);

    int insertSelective(BrandUpdateLog row);

    List<BrandUpdateLog> selectByExample(BrandUpdateLogExample example);

    BrandUpdateLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") BrandUpdateLog row, @Param("example") BrandUpdateLogExample example);

    int updateByExample(@Param("row") BrandUpdateLog row, @Param("example") BrandUpdateLogExample example);

    int updateByPrimaryKeySelective(BrandUpdateLog row);

    int updateByPrimaryKey(BrandUpdateLog row);
}