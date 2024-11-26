package com.itsthatjun.ecommerce.service;

import reactor.core.publisher.Mono;

import java.util.UUID;

public interface VerificationService {

    /**
     * Generate auth code and store it in redis
     * send out auth code to user to verify their mobile number
     */
    Mono<String> sendMobileVerification(UUID memberId, String mobile);

    /**
     * Verify auth code from user input
     */
    Mono<Boolean> verifyAuthCode(String authCode);

    /**
     * Generate verification token
     * send out verification token to user to verify their email
     */
    Mono<String> sendEmailVerification(UUID memberId, String email);

    /**
     * Verify verification token from user input
     */
    Mono<Boolean> verifyVerificationToken(String token);
}
