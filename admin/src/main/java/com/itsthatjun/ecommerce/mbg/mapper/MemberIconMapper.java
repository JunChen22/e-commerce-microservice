package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.MemberIcon;
import com.itsthatjun.ecommerce.mbg.model.MemberIconExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MemberIconMapper {
    long countByExample(MemberIconExample example);

    int deleteByExample(MemberIconExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MemberIcon row);

    int insertSelective(MemberIcon row);

    List<MemberIcon> selectByExample(MemberIconExample example);

    MemberIcon selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") MemberIcon row, @Param("example") MemberIconExample example);

    int updateByExample(@Param("row") MemberIcon row, @Param("example") MemberIconExample example);

    int updateByPrimaryKeySelective(MemberIcon row);

    int updateByPrimaryKey(MemberIcon row);
}