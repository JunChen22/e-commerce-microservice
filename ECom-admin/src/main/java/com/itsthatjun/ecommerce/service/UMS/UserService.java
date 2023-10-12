package com.itsthatjun.ecommerce.service.UMS;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    @ApiOperation(value ="")
    Mono<MemberDetail> getUser(int userId);

    @ApiOperation(value ="")
    Flux<Member> getAllUser();

    @ApiOperation(value ="")
    Flux<MemberLoginLog> listAllLoginFrequency(int userId);

    @ApiOperation(value ="")
    Mono<Member> createMember(Member member, String operator);

    @ApiOperation(value ="")
    Mono<Member> updateMemberInfo(Member member, String operator);

    @ApiOperation(value ="")
    Mono<Member> updateMemberStatus(Member member, String operator);

    @ApiOperation(value ="")
    Mono<Void> delete(int memberId, String operator);
}
