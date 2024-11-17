package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.MemberActivityLog;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MemberActivityLogRepository extends ReactiveCrudRepository<MemberActivityLog, Integer> {

    @Query("INSERT INTO member_activity_log (member_id, activity, platform_type, ip_address) " +
            "VALUES (:#{#log.memberId}, CAST(:#{#log.activity.getValue()} AS user_activity_type_enum), " +
            "CAST(:#{#log.platformType.getValue()} AS platform_type_enum), :#{#log.ipAddress}) RETURNING *")
    Mono<MemberActivityLog> saveLog(@Param("log") MemberActivityLog log);
}
