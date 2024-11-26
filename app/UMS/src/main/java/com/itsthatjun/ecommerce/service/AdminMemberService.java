package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.admin.AdminMemberDetail;
import com.itsthatjun.ecommerce.model.entity.Member;
import com.itsthatjun.ecommerce.model.entity.MemberActivityLog;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AdminMemberService {

    Flux<Member> listAllUser();

    Flux<Member> listUser(int pageNum, int pageSize);

    Mono<AdminMemberDetail> getMemberDetailByMemberId(UUID memberId);

    Flux<MemberActivityLog> getMemberActivityLog(UUID memberId);

    Mono<Member> createMember(Member newMember, String operator);

    Mono<Member> updateMemberInfo(Member updatedMember, String operator);

    Mono<Member> updateMemberStatus(Member updatedMember, String operator);

    Mono<Member> deleteMember(UUID memberId, String operator);

    /**
     * Send message to user from admin
     * @param memberId
     * @param message
     * @param operator
     * @return
     */
    Mono<Void> sendUserNotification(UUID memberId, String message, String operator);

    /**
     * Send message to all users from admin
     * @param message
     * @param operator
     * @return
     */
    Mono<Void> sendAllUserNotification(String message, String operator);
}
