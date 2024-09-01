package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.dto.admin.AdminMemberDetail;
import com.itsthatjun.ecommerce.dto.model.AddressDTO;
import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MemberService {

    @ApiOperation("")       // TODO: with redis and sending email
    String generateAuthCode(String telephone);

    @ApiOperation("")      // TODO: use redis to send out auth code
    String verifyAuthCode(String telephone, String authCode);

    @ApiOperation("")
    Mono<MemberDetail> register(MemberDetail memberDetail);

    @ApiOperation("")
    Mono<MemberDetail> getInfo();

    @ApiOperation("")
    Mono<Member> updatePassword(int userId, String newPassword);

    @ApiOperation("password, name, icon")
    Mono<Member> updateInfo(MemberDetail memberDetail, int userId);

    @ApiOperation("")
    Mono<Address> updateAddress(int userId, AddressDTO newAddress);

    @ApiOperation("")
    Mono<Void> deleteAccount(int userId);

    @ApiOperation("Update from Auth server")
    Mono<Void> createLoginLog(MemberLoginLog loginLog);

    // ================= Admin operations ==================

    @ApiOperation("")
    Flux<Member> getAllUser();

    @ApiOperation("")
    Mono<AdminMemberDetail> getMemberDetailByUserId(int userId);

    @ApiOperation("")
    Flux<MemberLoginLog> getMemberLoginFrequency(int userId);

    @ApiOperation("")
    Mono<Member> createMember(Member newMember, String operator);

    @ApiOperation("")
    Mono<Member> updateMemberInfo(Member updatedMember, String operator);

    @ApiOperation("")
    Mono<Member> updateMemberStatus(Member updatedMember, String operator);

    @ApiOperation("")
    Mono<Member> deleteMember(int userId, String operator);

    @ApiOperation("Send message to user from admin")
    Mono<Void> sendUserNotification(int userId, String message, String operator);

    @ApiOperation("Send message to user from admin")
    Mono<Void> sendAllUserNotification(String message, String operator);
}
