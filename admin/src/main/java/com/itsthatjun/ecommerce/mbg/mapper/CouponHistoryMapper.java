package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.CouponHistory;
import com.itsthatjun.ecommerce.mbg.model.CouponHistoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CouponHistoryMapper {
    long countByExample(CouponHistoryExample example);

    int deleteByExample(CouponHistoryExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CouponHistory row);

    int insertSelective(CouponHistory row);

    List<CouponHistory> selectByExample(CouponHistoryExample example);

    CouponHistory selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") CouponHistory row, @Param("example") CouponHistoryExample example);

    int updateByExample(@Param("row") CouponHistory row, @Param("example") CouponHistoryExample example);

    int updateByPrimaryKeySelective(CouponHistory row);

    int updateByPrimaryKey(CouponHistory row);
}