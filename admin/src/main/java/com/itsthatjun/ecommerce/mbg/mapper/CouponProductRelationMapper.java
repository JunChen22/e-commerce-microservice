package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.CouponProductRelation;
import com.itsthatjun.ecommerce.mbg.model.CouponProductRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CouponProductRelationMapper {
    long countByExample(CouponProductRelationExample example);

    int deleteByExample(CouponProductRelationExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CouponProductRelation row);

    int insertSelective(CouponProductRelation row);

    List<CouponProductRelation> selectByExample(CouponProductRelationExample example);

    CouponProductRelation selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") CouponProductRelation row, @Param("example") CouponProductRelationExample example);

    int updateByExample(@Param("row") CouponProductRelation row, @Param("example") CouponProductRelationExample example);

    int updateByPrimaryKeySelective(CouponProductRelation row);

    int updateByPrimaryKey(CouponProductRelation row);
}