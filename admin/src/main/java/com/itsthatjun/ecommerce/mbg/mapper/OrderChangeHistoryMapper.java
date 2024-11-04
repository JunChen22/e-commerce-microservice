package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.OrderChangeHistory;
import com.itsthatjun.ecommerce.mbg.model.OrderChangeHistoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OrderChangeHistoryMapper {
    long countByExample(OrderChangeHistoryExample example);

    int deleteByExample(OrderChangeHistoryExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(OrderChangeHistory row);

    int insertSelective(OrderChangeHistory row);

    List<OrderChangeHistory> selectByExample(OrderChangeHistoryExample example);

    OrderChangeHistory selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") OrderChangeHistory row, @Param("example") OrderChangeHistoryExample example);

    int updateByExample(@Param("row") OrderChangeHistory row, @Param("example") OrderChangeHistoryExample example);

    int updateByPrimaryKeySelective(OrderChangeHistory row);

    int updateByPrimaryKey(OrderChangeHistory row);
}