package com.itsthatjun.ecommerce.service;

import com.itsthatjun.ecommerce.dto.AuthResponse;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import reactor.core.publisher.Mono;

public interface JWTTokenService {

    /**
     * Generates a new access token and refresh token
     *
     * @param userDetails the user details
     * @return a new access token and refresh token
     */
    Mono<AuthResponse> generateToken(CustomUserDetail userDetails);

    /**
     * Validates the refresh token and returns a new access token
     *
     * @param refreshToken the refresh token
     * @return a new access token
     */
    Mono<AuthResponse> validateAndRefreshToken(String refreshToken);
}
