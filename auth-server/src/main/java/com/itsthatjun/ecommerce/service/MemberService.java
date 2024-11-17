package com.itsthatjun.ecommerce.service;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface MemberService {

    /**
     * Sends update log to UMS.
     *
     * @param memberId the ID of the user to log
     * @return a Mono indicating completion
     */
    Mono<Void> memberLoginLog(UUID memberId);

    /**
     * Sends logout log to UMS.
     *
     * @param memberId the ID of the user to log
     * @return a Mono indicating completion
     */
    Mono<Void> memberLogoutLog(UUID memberId);
}
