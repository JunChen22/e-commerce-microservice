package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import com.itsthatjun.ecommerce.mbg.model.ReturnRequestExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ReturnRequestMapper {
    long countByExample(ReturnRequestExample example);

    int deleteByExample(ReturnRequestExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(ReturnRequest row);

    int insertSelective(ReturnRequest row);

    List<ReturnRequest> selectByExample(ReturnRequestExample example);

    ReturnRequest selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") ReturnRequest row, @Param("example") ReturnRequestExample example);

    int updateByExample(@Param("row") ReturnRequest row, @Param("example") ReturnRequestExample example);

    int updateByPrimaryKeySelective(ReturnRequest row);

    int updateByPrimaryKey(ReturnRequest row);
}