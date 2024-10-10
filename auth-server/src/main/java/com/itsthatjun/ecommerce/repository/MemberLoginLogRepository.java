package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.MemberLoginLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface MemberLoginLogRepository extends ReactiveCrudRepository<MemberLoginLog, Integer> {

    /**
     * Find all login logs of a member.
     * @param memberId the ID of the member
     * @return a Flux object that may contain the login logs
     */
    Flux<MemberLoginLog> findByMemberId(Integer memberId);
}
