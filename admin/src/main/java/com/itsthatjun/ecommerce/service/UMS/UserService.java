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
    Flux<Member> listAllUser();

    @ApiOperation("")
    Flux<MemberLoginLog> listAllLoginFrequency(int userId);

    @ApiOperation("")
    Mono<Member> createMember(Member member);

    @ApiOperation("")
    Mono<Member> updateMemberInfo(Member member);

    @ApiOperation("")
    Mono<Member> updateMemberStatus(Member member);

    @ApiOperation("")
    Mono<Void> delete(int memberId);
}
