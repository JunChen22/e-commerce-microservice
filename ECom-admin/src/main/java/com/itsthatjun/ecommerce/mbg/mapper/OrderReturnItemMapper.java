package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.OrderReturnItem;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderReturnItemMapper {
    long countByExample(OrderReturnItemExample example);

    int deleteByExample(OrderReturnItemExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OrderReturnItem record);

    int insertSelective(OrderReturnItem record);

    List<OrderReturnItem> selectByExample(OrderReturnItemExample example);

    OrderReturnItem selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") OrderReturnItem record, @Param("example") OrderReturnItemExample example);

    int updateByExample(@Param("record") OrderReturnItem record, @Param("example") OrderReturnItemExample example);

    int updateByPrimaryKeySelective(OrderReturnItem record);

    int updateByPrimaryKey(OrderReturnItem record);
}