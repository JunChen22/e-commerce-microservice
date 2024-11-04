package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.RolePermissionRelation;
import com.itsthatjun.ecommerce.mbg.model.RolePermissionRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface RolePermissionRelationMapper {
    long countByExample(RolePermissionRelationExample example);

    int deleteByExample(RolePermissionRelationExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(RolePermissionRelation row);

    int insertSelective(RolePermissionRelation row);

    List<RolePermissionRelation> selectByExample(RolePermissionRelationExample example);

    RolePermissionRelation selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") RolePermissionRelation row, @Param("example") RolePermissionRelationExample example);

    int updateByExample(@Param("row") RolePermissionRelation row, @Param("example") RolePermissionRelationExample example);

    int updateByPrimaryKeySelective(RolePermissionRelation row);

    int updateByPrimaryKey(RolePermissionRelation row);
}