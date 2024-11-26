package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.enums.status.AccountStatus;
import com.itsthatjun.ecommerce.enums.status.LifeCycleStatus;
import com.itsthatjun.ecommerce.model.entity.Member;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface MemberRepository extends ReactiveCrudRepository<Member, UUID> {

    @Query("SELECT count(*) FROM member WHERE email = ':email'")
    Mono<Boolean> existsByEmail( @Param("email") String email);

    @Query("SELECT * FROM member OFFSET :offset LIMIT :limit")
    Flux<Member> findAllWithPage(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * Find all active members and not deleted. used for email notification
     * @return a list of active members
     */
    @Query("")
    Flux<Member> findAllActiveMember();

    @Query("SELECT * FROM member WHERE id = ':memberId' AND lifecycle_status = 'normal' AND status = 'ACTIVE'")
    Mono<Member> findById(UUID memberId);

    @Query("")
    Mono<Member> findByEmail(String email);

    @Query("")
    Mono<Member> saveMember(Member member);

    @Query("")
    Mono<Member> updateMemberPassword(Member member);

    @Query("")
    Mono<Member> updateMemberInfo(Member member);

    @Query("")
    Mono<Member> updateMemberVerification(Member member);

    @Query("")
    Mono<Member> updateMemberStatus(Member member);

    @Query("")
    //member.setLifecycleStatus(LifeCycleStatus.SOFT_DELETE);
    //member.setStatus(AccountStatus.INACTIVE);
    Mono<Void> deleteMember(UUID memberId);
}
