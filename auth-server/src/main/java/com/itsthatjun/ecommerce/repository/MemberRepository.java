package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.Member;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface MemberRepository extends ReactiveCrudRepository<Member, UUID> {

    /**
     * Find a member by username. Members with normal, active status.
     * @param username the username of the member
     * @return a Mono object that may contain the member
     */
    @Query("SELECT * FROM member " +
            "WHERE username = :username AND status = 'active' AND lifecycle_status = 'normal'")
    Mono<Member> findByUsername(String username);

    /**
     * save member
     * @param member
     * @return a Mono object that may contain the member
     */
    @Query("INSERT INTO member (id, username, password, name, phone_number, email, email_subscription, status, " +
            "verified_status, lifecycle_status, created_at, last_login, platform_type) " +
            "VALUES (:#{#member.id}, :#{#member.username}, :#{#member.password}, :#{#member.name}, :#{#member.phoneNumber}, " +
            ":#{#member.email}, :#{#member.emailSubscription}, CAST(:#{#member.status.getValue()} AS account_status_enum), " +
            "CAST(:#{#member.verifiedStatus.getValue()} AS verification_status_enum), " +
            "CAST(:#{#member.lifecycleStatus.getValue()} AS lifecycle_status_enum), :#{#member.createdAt}, " +
            ":#{#member.lastLogin}, CAST(:#{#member.platformType.getValue()} AS platform_type_enum)) RETURNING *")
    Mono<Member> saveMember(@Param("member") Member member);

    /**
     * update member information
     * @param id the ID of the member
     * @return a Mono object that may contain the member
     */
    @Query("UPDATE member " +
            "SET username = :#{#member.username}, " +
            "password = :#{#member.password}, " +
            "name = :#{#member.name}, " +
            "phone_number = :#{#member.phoneNumber}, " +
            "email = :#{#member.email}, " +
            "email_subscription = :#{#member.emailSubscription}, " +
            "last_login = :#{#member.lastLogin}" +
            "WHERE id = :#{#member.id} " +
            "RETURNING *")
    Mono<Member> updateInfo(@Param("member") Member member);

    /**
     * Update member status and lifecycle status (e.g., activate, deactivate, soft delete)
     * @param member the member with updated status and lifecycle information
     * @return a Mono containing the updated member
     */
    @Query("UPDATE member " +
            "SET status = CAST(:#{#member.status.getValue()} AS account_status_enum), " +
            "lifecycle_status = CAST(:#{#member.lifecycleStatus.getValue()} AS lifecycle_status_enum) " +
            "WHERE id = :#{#member.id} RETURNING *")
    Mono<Member> updateStatus(@Param("member") Member member);

     /** TODO: delete and soft delete
     * Soft delete a member by marking status and lifecycle status
     * @param memberId the ID of the member to soft delete
     * @return a Mono containing the updated member
     */
    @Query("UPDATE member " +
            "SET status = 'inactive', " +
            "lifecycle_status = 'soft_delete' " +
            "WHERE id = :memberId")
    Mono<Void> deleteMember(@Param("memberId") UUID memberId);
}
