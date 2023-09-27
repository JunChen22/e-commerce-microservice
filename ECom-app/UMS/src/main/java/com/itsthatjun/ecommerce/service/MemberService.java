package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface MemberService {

    @ApiOperation(value = "")       // TODO: with redis and sending email
    String generateAuthCode(String telephone);

    @ApiOperation(value = "")      // TODO: use redis to send out auth code
    String verifyAuthCode(String telephone, String authCode);

    @ApiOperation(value = "")
    Mono<MemberDetail> register(MemberDetail memberDetail);

    @ApiOperation(value = "")
    Mono<MemberDetail> getInfo(int userId);

    @ApiOperation(value = "")
    Mono<Member> updatePassword(int userId, String newPassword);

    @ApiOperation(value = "passowrd, name, icon")
    Mono<Member> updateInfo(MemberDetail memberDetail);

    @ApiOperation(value = "")
    Mono<Address> updateAddress(int userId, Address newAddress);

    @ApiOperation(value = "")
    Mono<Void> deleteAccount(int userId);

    @ApiOperation(value = "Update from Auth server")
    Mono<Void> createLoginLog(MemberLoginLog loginLog);

    // ================= Admin operations ==================

    @ApiOperation(value = "")
    Mono<Member> createMember(Member newMember);

    @ApiOperation(value = "")
    Flux<Member> getAllUser();

    @ApiOperation(value = "")
    Mono<MemberDetail> getMemberDetailByUserId(int userId);

    @ApiOperation(value = "")
    Flux<MemberLoginLog> getMemberLoginFrequency(int userId);

    @ApiOperation(value = "")
    Mono<Member> updateMemberInfo(Member updatedMember);

    @ApiOperation(value = "")
    Mono<Member> updateMemberStatus(Member updatedMember);

    @ApiOperation(value = "")
    Mono<Member> deleteMember(int userId);
}
