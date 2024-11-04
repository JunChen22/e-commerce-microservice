package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MemberMapper {
    long countByExample(MemberExample example);

    int deleteByExample(MemberExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Member row);

    int insertSelective(Member row);

    List<Member> selectByExample(MemberExample example);

    Member selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") Member row, @Param("example") MemberExample example);

    int updateByExample(@Param("row") Member row, @Param("example") MemberExample example);

    int updateByPrimaryKeySelective(Member row);

    int updateByPrimaryKey(Member row);
}