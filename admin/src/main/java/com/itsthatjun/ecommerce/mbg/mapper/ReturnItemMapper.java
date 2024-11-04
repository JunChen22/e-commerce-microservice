package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ReturnItem;
import com.itsthatjun.ecommerce.mbg.model.ReturnItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ReturnItemMapper {
    long countByExample(ReturnItemExample example);

    int deleteByExample(ReturnItemExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ReturnItem row);

    int insertSelective(ReturnItem row);

    List<ReturnItem> selectByExample(ReturnItemExample example);

    ReturnItem selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ReturnItem row, @Param("example") ReturnItemExample example);

    int updateByExample(@Param("row") ReturnItem row, @Param("example") ReturnItemExample example);

    int updateByPrimaryKeySelective(ReturnItem row);

    int updateByPrimaryKey(ReturnItem row);
}