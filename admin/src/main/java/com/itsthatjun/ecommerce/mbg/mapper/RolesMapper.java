package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.Roles;
import com.itsthatjun.ecommerce.mbg.model.RolesExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RolesMapper {
    long countByExample(RolesExample example);

    int deleteByExample(RolesExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(Roles row);

    int insertSelective(Roles row);

    List<Roles> selectByExample(RolesExample example);

    Roles selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") Roles row, @Param("example") RolesExample example);

    int updateByExample(@Param("row") Roles row, @Param("example") RolesExample example);

    int updateByPrimaryKeySelective(Roles row);

    int updateByPrimaryKey(Roles row);
}