package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MemberLoginLogMapper {
    long countByExample(MemberLoginLogExample example);

    int deleteByExample(MemberLoginLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MemberLoginLog row);

    int insertSelective(MemberLoginLog row);

    List<MemberLoginLog> selectByExample(MemberLoginLogExample example);

    MemberLoginLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") MemberLoginLog row, @Param("example") MemberLoginLogExample example);

    int updateByExample(@Param("row") MemberLoginLog row, @Param("example") MemberLoginLogExample example);

    int updateByPrimaryKeySelective(MemberLoginLog row);

    int updateByPrimaryKey(MemberLoginLog row);
}