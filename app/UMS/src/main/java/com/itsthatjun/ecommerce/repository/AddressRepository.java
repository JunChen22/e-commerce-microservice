package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.Address;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface AddressRepository extends ReactiveCrudRepository<Address, Integer> {

    Mono<Address> findByMemberId(UUID memberId);
}
