package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.CouponChangeLog;
import com.itsthatjun.ecommerce.mbg.model.CouponChangeLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CouponChangeLogMapper {
    long countByExample(CouponChangeLogExample example);

    int deleteByExample(CouponChangeLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CouponChangeLog row);

    int insertSelective(CouponChangeLog row);

    List<CouponChangeLog> selectByExample(CouponChangeLogExample example);

    CouponChangeLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") CouponChangeLog row, @Param("example") CouponChangeLogExample example);

    int updateByExample(@Param("row") CouponChangeLog row, @Param("example") CouponChangeLogExample example);

    int updateByPrimaryKeySelective(CouponChangeLog row);

    int updateByPrimaryKey(CouponChangeLog row);
}