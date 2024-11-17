package com.itsthatjun.ecommerce.service.eventupdate;

import com.itsthatjun.ecommerce.model.entity.Member;
import com.itsthatjun.ecommerce.repository.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class UmsEventUpdateService {

    private static final Logger LOG = LoggerFactory.getLogger(UmsEventUpdateService.class);

    private final MemberRepository memberRepository;

    @Autowired
    public UmsEventUpdateService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Adds a new user to the authentication database.
     *
     * @param member the member to add
     * @return a Mono containing the added member
     */
    public Mono<Member> addMember(Member member) {
        return memberRepository.saveMember(member);
    }

    /**
     * Updates user information like password and name.
     *
     * @param member the member with updated information
     * @return a Mono containing the updated member
     */
    public Mono<Member> updateInfo(Member member) {
        return memberRepository.updateInfo(member);
    }

    /**
     * Updates the account status. Only status 1 allows login.
     *
     * @param member the member with updated status
     * @return a Mono containing the updated member
     */
    public Mono<Member> updateStatus(Member member) {
        return memberRepository.updateStatus(member);
    }

    /**
     * Deletes a member from the authentication server.
     *
     * @param memberId the ID of the member to delete
     * @return a Mono indicating completion
     */
    public Mono<Void> deleteMember(UUID memberId) {
        return memberRepository.deleteMember(memberId);
    }
}
