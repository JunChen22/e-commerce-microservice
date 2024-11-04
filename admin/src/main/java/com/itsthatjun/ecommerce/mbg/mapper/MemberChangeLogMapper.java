package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.MemberChangeLog;
import com.itsthatjun.ecommerce.mbg.model.MemberChangeLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MemberChangeLogMapper {
    long countByExample(MemberChangeLogExample example);

    int deleteByExample(MemberChangeLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MemberChangeLog row);

    int insertSelective(MemberChangeLog row);

    List<MemberChangeLog> selectByExample(MemberChangeLogExample example);

    MemberChangeLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") MemberChangeLog row, @Param("example") MemberChangeLogExample example);

    int updateByExample(@Param("row") MemberChangeLog row, @Param("example") MemberChangeLogExample example);

    int updateByPrimaryKeySelective(MemberChangeLog row);

    int updateByPrimaryKey(MemberChangeLog row);
}