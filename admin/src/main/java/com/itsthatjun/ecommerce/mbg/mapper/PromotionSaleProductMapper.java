package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.PromotionSaleProduct;
import com.itsthatjun.ecommerce.mbg.model.PromotionSaleProductExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PromotionSaleProductMapper {
    long countByExample(PromotionSaleProductExample example);

    int deleteByExample(PromotionSaleProductExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PromotionSaleProduct row);

    int insertSelective(PromotionSaleProduct row);

    List<PromotionSaleProduct> selectByExample(PromotionSaleProductExample example);

    PromotionSaleProduct selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") PromotionSaleProduct row, @Param("example") PromotionSaleProductExample example);

    int updateByExample(@Param("row") PromotionSaleProduct row, @Param("example") PromotionSaleProductExample example);

    int updateByPrimaryKeySelective(PromotionSaleProduct row);

    int updateByPrimaryKey(PromotionSaleProduct row);
}