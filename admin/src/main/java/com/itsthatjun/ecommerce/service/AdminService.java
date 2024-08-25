package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.Admin;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Mono;

public interface AdminService {

    @ApiOperation(value = "")
    Mono<String> login(String username, String password);

    @ApiOperation(value = "")
    String register(Admin admin);

    @ApiOperation(value = "")
    Admin getAdminByUsername(String username);
}
