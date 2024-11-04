package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.AdminRoleRelation;
import com.itsthatjun.ecommerce.mbg.model.AdminRoleRelationExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AdminRoleRelationMapper {
    long countByExample(AdminRoleRelationExample example);

    int deleteByExample(AdminRoleRelationExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AdminRoleRelation row);

    int insertSelective(AdminRoleRelation row);

    List<AdminRoleRelation> selectByExample(AdminRoleRelationExample example);

    AdminRoleRelation selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") AdminRoleRelation row, @Param("example") AdminRoleRelationExample example);

    int updateByExample(@Param("row") AdminRoleRelation row, @Param("example") AdminRoleRelationExample example);

    int updateByPrimaryKeySelective(AdminRoleRelation row);

    int updateByPrimaryKey(AdminRoleRelation row);
}