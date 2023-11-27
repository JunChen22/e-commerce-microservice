package com.itsthatjun.ecommerce.service.UMS;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import io.swagger.annotations.ApiOperation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    @ApiOperation("")
    Mono<MemberDetail> getUser(int userId);

    @ApiOperation("")
    Flux<Member> getAllUser();

    @ApiOperation("")
    Flux<MemberLoginLog> listAllLoginFrequency(int userId);

    @ApiOperation("")
    Mono<Member> createMember(Member member, String operator);

    @ApiOperation("")
    Mono<Member> updateMemberInfo(Member member, String operator);

    @ApiOperation("")
    Mono<Member> updateMemberStatus(Member member, String operator);

    @ApiOperation("")
    Mono<Void> delete(int memberId, String operator);
}
