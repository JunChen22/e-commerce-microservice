package com.itsthatjun.ecommerce.repository;

import com.itsthatjun.ecommerce.model.entity.RefreshTokens;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends ReactiveCrudRepository<RefreshTokens, Integer> {

    /**
     * Find a refresh token by token string
     * @param refreshToken the refresh token string
     * @return a Mono object that may contain the refresh token
     */
    Mono<RefreshTokens> findByRefreshToken(String refreshToken);

    /**
     * Delete refresh tokens for a specific user
     * @param memberId the ID of the member
     * @return a Mono object that may contain the member
     */
    Mono<Void> deleteByMemberId(UUID memberId);
}
