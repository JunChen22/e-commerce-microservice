package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.mbg.model.Member;
import io.swagger.annotations.ApiOperation;

public interface MemberService {

    @ApiOperation(value = "")
    String login(String username, String password);

    @ApiOperation(value = "Get user by username")
    Member getMemberByUsername(String username);

    @ApiOperation(value = "adds user to auth datase")
    String addMember(Member member);

    @ApiOperation(value = "update info like password and name, other info are irrelevant for auth")
    String updateInfo(Member member);

    @ApiOperation(value = "Update account status, status 0 can not login only 1 can.")
    String updateStatus(Member member);

    @ApiOperation(value = "delete member, remove from auth server, record kept in UMS")
    void deleteMember(int userId);
}
