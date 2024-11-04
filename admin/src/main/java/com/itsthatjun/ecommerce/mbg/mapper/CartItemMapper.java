package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.CartItem;
import com.itsthatjun.ecommerce.mbg.model.CartItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CartItemMapper {
    long countByExample(CartItemExample example);

    int deleteByExample(CartItemExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(CartItem row);

    int insertSelective(CartItem row);

    List<CartItem> selectByExample(CartItemExample example);

    CartItem selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") CartItem row, @Param("example") CartItemExample example);

    int updateByExample(@Param("row") CartItem row, @Param("example") CartItemExample example);

    int updateByPrimaryKeySelective(CartItem row);

    int updateByPrimaryKey(CartItem row);
}