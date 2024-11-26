package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.MemberActivityLog;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface MemberActivityLogRepository extends ReactiveCrudRepository<MemberActivityLog, Integer> {

    @Query("SELECT * FROM member_activity_log WHERE member_id = :memberId ORDER BY created_at DESC")
    Flux<MemberActivityLog> findAllByMemberId(UUID memberId);

    @Query("INSERT INTO member_activity_log (member_id, activity_type, activity_description) VALUES (:#{#memberActivityLog.memberId}, " +
            ":#{#memberActivityLog.activityType}, :#{#memberActivityLog.activityDescription}) RETURNING *")
    Mono<MemberActivityLog> saveLog(MemberActivityLog memberActivityLog);
}
