package com.itsthatjun.ecommerce.service;


import com.itsthatjun.ecommerce.mbg.model.Admin;
import com.itsthatjun.ecommerce.mbg.model.Permission;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;

import java.util.List;

public interface AdminService {

    @ApiOperation(value = "")
    String login(String username, String password);

    @ApiOperation(value = "")
    String register(Admin admin);

    @ApiOperation(value = "")
    Admin getAdminByUsername(String username);
}
