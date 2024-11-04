package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.PromotionSale;
import com.itsthatjun.ecommerce.mbg.model.PromotionSaleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface PromotionSaleMapper {
    long countByExample(PromotionSaleExample example);

    int deleteByExample(PromotionSaleExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(PromotionSale row);

    int insertSelective(PromotionSale row);

    List<PromotionSale> selectByExample(PromotionSaleExample example);

    PromotionSale selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") PromotionSale row, @Param("example") PromotionSaleExample example);

    int updateByExample(@Param("row") PromotionSale row, @Param("example") PromotionSaleExample example);

    int updateByPrimaryKeySelective(PromotionSale row);

    int updateByPrimaryKey(PromotionSale row);
}