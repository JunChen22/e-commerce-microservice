package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dao.domainmodel.AdminDetail;
import com.itsthatjun.ecommerce.mbg.model.Admin;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Mono;

public interface AdminService {

    @ApiOperation(value = "main admin register an admin with role and authority")
    Mono<String> register(Admin admin);

    @ApiOperation(value = "main admin list an admins roles")
    Mono<AdminDetail> getAdminDetail(int id);

}