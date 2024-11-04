package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.AdminPermissionRelation;
import com.itsthatjun.ecommerce.mbg.model.AdminPermissionRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AdminPermissionRelationMapper {
    long countByExample(AdminPermissionRelationExample example);

    int deleteByExample(AdminPermissionRelationExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AdminPermissionRelation row);

    int insertSelective(AdminPermissionRelation row);

    List<AdminPermissionRelation> selectByExample(AdminPermissionRelationExample example);

    AdminPermissionRelation selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") AdminPermissionRelation row, @Param("example") AdminPermissionRelationExample example);

    int updateByExample(@Param("row") AdminPermissionRelation row, @Param("example") AdminPermissionRelationExample example);

    int updateByPrimaryKeySelective(AdminPermissionRelation row);

    int updateByPrimaryKey(AdminPermissionRelation row);
}