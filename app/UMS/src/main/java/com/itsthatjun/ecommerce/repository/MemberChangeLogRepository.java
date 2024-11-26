package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.MemberChangeLog;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface MemberChangeLogRepository extends ReactiveCrudRepository<MemberChangeLog, Integer> {

    @Query("INSERT INTO member_change_log (member_id, change_type, change_description) VALUES (:#{#memberChangeLog.memberId}, " +
            ":#{#memberChangeLog.changeType}, :#{#memberChangeLog.changeDescription}) RETURNING *")
    Mono<MemberChangeLog> saveLog(MemberChangeLog memberChangeLog);
}
