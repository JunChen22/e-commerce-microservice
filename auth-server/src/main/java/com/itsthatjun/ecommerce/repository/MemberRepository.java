package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.Member;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface MemberRepository extends ReactiveCrudRepository<Member, Integer> {

    /**
     * Find a member by username.
     * @param username the username of the member
     * @return a Mono object that may contain the member
     */
    Mono<Member> findByUsername(String username);

    Flux<Member> findAll();
}
