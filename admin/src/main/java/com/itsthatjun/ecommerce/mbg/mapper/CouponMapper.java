package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.Coupon;
import com.itsthatjun.ecommerce.mbg.model.CouponExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CouponMapper {
    long countByExample(CouponExample example);

    int deleteByExample(CouponExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Coupon row);

    int insertSelective(Coupon row);

    List<Coupon> selectByExample(CouponExample example);

    Coupon selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") Coupon row, @Param("example") CouponExample example);

    int updateByExample(@Param("row") Coupon row, @Param("example") CouponExample example);

    int updateByPrimaryKeySelective(Coupon row);

    int updateByPrimaryKey(Coupon row);
}