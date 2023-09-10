package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.Member;
import io.swagger.annotations.ApiOperation;

public interface MemberService {

    @ApiOperation(value = "")
    String generateAuthCode(String telephone);

    @ApiOperation(value = "")
    String verifyAuthCode(String telephone, String authCode);

    @ApiOperation(value = "")
    String login(String username, String password);

    @ApiOperation(value = "")
    String register(Member member);

    // TODO: use redis to send out auth code
    @ApiOperation(value = "")
    void updatePassword();

    @ApiOperation(value = "")
    Member getMemberByUserName(String username);
}
