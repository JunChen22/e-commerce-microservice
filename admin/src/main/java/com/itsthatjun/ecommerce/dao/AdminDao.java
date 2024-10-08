package com.itsthatjun.ecommerce.dao;

import com.itsthatjun.ecommerce.mbg.model.Admin;
import com.itsthatjun.ecommerce.mbg.model.AdminLoginLog;
import com.itsthatjun.ecommerce.mbg.model.Permission;
import com.itsthatjun.ecommerce.mbg.model.Roles;
import io.swagger.annotations.ApiModelProperty;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AdminDao {

    @ApiModelProperty(value = "")
    List<Permission> getPermissionList(@Param("id") int id);

    @ApiModelProperty(value = "")
    List<Roles> getRolesList(@Param("id") int id);

    @ApiModelProperty(value = "")
    List<AdminLoginLog> getLoginList(@Param("id") int id);
}
