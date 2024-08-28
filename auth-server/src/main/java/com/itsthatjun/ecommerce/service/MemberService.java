package com.itsthatjun.ecommerce.service;

import io.swagger.annotations.ApiOperation;

public interface MemberService {

    @ApiOperation(value = "sends update log to UMS")
    void memberLoginLog(int userId);
}
