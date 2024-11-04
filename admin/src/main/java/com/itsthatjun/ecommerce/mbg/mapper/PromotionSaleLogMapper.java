package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.PromotionSaleLog;
import com.itsthatjun.ecommerce.mbg.model.PromotionSaleLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PromotionSaleLogMapper {
    long countByExample(PromotionSaleLogExample example);

    int deleteByExample(PromotionSaleLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PromotionSaleLog row);

    int insertSelective(PromotionSaleLog row);

    List<PromotionSaleLog> selectByExample(PromotionSaleLogExample example);

    PromotionSaleLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") PromotionSaleLog row, @Param("example") PromotionSaleLogExample example);

    int updateByExample(@Param("row") PromotionSaleLog row, @Param("example") PromotionSaleLogExample example);

    int updateByPrimaryKeySelective(PromotionSaleLog row);

    int updateByPrimaryKey(PromotionSaleLog row);
}