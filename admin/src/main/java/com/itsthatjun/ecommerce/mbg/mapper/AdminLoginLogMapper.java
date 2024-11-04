package com.itsthatjun.ecommerce.mbg.mapper;

import com.itsthatjun.ecommerce.mbg.model.AdminLoginLog;
import com.itsthatjun.ecommerce.mbg.model.AdminLoginLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface AdminLoginLogMapper {
    long countByExample(AdminLoginLogExample example);

    int deleteByExample(AdminLoginLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(AdminLoginLog row);

    int insertSelective(AdminLoginLog row);

    List<AdminLoginLog> selectByExample(AdminLoginLogExample example);

    AdminLoginLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("row") AdminLoginLog row, @Param("example") AdminLoginLogExample example);

    int updateByExample(@Param("row") AdminLoginLog row, @Param("example") AdminLoginLogExample example);

    int updateByPrimaryKeySelective(AdminLoginLog row);

    int updateByPrimaryKey(AdminLoginLog row);
}