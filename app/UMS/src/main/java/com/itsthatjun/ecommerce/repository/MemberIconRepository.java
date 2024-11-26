package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.MemberIcon;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface MemberIconRepository extends ReactiveCrudRepository<MemberIcon, Integer> {

    Mono<MemberIcon> findByMemberId(UUID memberId);
}
