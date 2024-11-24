package com.itsthatjun.ecommerce.service.impl;

import com.itsthatjun.ecommerce.dto.AuthResponse;
import com.itsthatjun.ecommerce.model.entity.RefreshTokens;
import com.itsthatjun.ecommerce.repository.RefreshTokenRepository;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.security.jwt.JwtTokenUtil;
import com.itsthatjun.ecommerce.service.JWTTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class JWTTokenServiceImpl implements JWTTokenService {

    private static final Logger LOG = LoggerFactory.getLogger(JWTTokenServiceImpl.class);

    private final RefreshTokenRepository refreshTokenRepository;

    private final MemberServiceImpl memberService;

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.refreshExpirationTimeMinute}")
    private int refreshExpiration;  // Refresh token expiration in minutes

    @Autowired
    public JWTTokenServiceImpl(RefreshTokenRepository refreshTokenRepository, MemberServiceImpl memberService, JwtTokenUtil jwtTokenUtil) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.memberService = memberService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Mono<AuthResponse> generateToken(CustomUserDetail userDetails) {
        return jwtTokenUtil.generateToken(userDetails)
                .flatMap(accessToken -> jwtTokenUtil.generateRefreshToken(userDetails)
                        .flatMap(refreshToken -> {
                            RefreshTokens token = new RefreshTokens();
                            token.setRefreshToken(refreshToken);
                            token.setMemberId(userDetails.getMemberId());
                            token.setExpiryDate(calculateNewExpiry());

                            return refreshTokenRepository.save(token)
                                    .map(savedToken -> new AuthResponse(true, accessToken, refreshToken));
                        }));
    }

    @Override
    public Mono<AuthResponse> validateAndRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .filter(foundRefreshToken -> foundRefreshToken.getExpiryDate().isAfter(LocalDateTime.now())) // Check expiration
                .flatMap(foundRefreshToken -> jwtTokenUtil.getUsernameFromToken(refreshToken)
                        .flatMap(userName -> memberService.findByUsername(userName)
                                .flatMap(user -> Mono.zip(
                                        jwtTokenUtil.generateToken((CustomUserDetail) user),
                                        jwtTokenUtil.generateRefreshToken((CustomUserDetail) user)
                                ).flatMap(tuple -> {
                                    String newAccessToken = tuple.getT1();
                                    String newRefreshToken = tuple.getT2();

                                    foundRefreshToken.setRefreshToken(newRefreshToken);
                                    foundRefreshToken.setExpiryDate(calculateNewExpiry());

                                    // update the token
                                    return refreshTokenRepository.save(foundRefreshToken)
                                            .map(savedToken -> new AuthResponse(true, newAccessToken, newRefreshToken));
                                }))
                        ))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Invalid or expired refresh token")));
    }

    private LocalDateTime calculateNewExpiry() {
        return LocalDateTime.now().plusMinutes(refreshExpiration);
    }
}
