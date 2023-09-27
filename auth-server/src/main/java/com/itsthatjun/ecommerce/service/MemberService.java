package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.Member;
import io.swagger.annotations.ApiOperation;

public interface MemberService {

    @ApiOperation(value = "")
    String login(String username, String password);

    @ApiOperation(value = "Get user by username")
    Member getMemberByUsername(String username);
}
