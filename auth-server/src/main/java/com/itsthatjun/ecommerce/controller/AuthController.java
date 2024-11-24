package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.AuthResponse;
import com.itsthatjun.ecommerce.dto.LoginRequest;
import com.itsthatjun.ecommerce.dto.TokenRefreshRequest;
import com.itsthatjun.ecommerce.enums.type.PlatformType;
import com.itsthatjun.ecommerce.enums.type.UserActivityType;
import com.itsthatjun.ecommerce.security.CustomReactiveAuthenticationManager;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import com.itsthatjun.ecommerce.service.impl.JWTTokenServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "Auth related", description = "Login, logoff and refresh token")
public class AuthController {

    private final CustomReactiveAuthenticationManager authenticationManager;

    private final MemberServiceImpl memberService;

    private final JWTTokenServiceImpl tokenService;

    @Autowired
    public AuthController(CustomReactiveAuthenticationManager authenticationManager, MemberServiceImpl memberService,
                          JWTTokenServiceImpl tokenService) {
        this.authenticationManager = authenticationManager;
        this.memberService = memberService;
        this.tokenService = tokenService;
    }

    @Operation(summary = "Login", description = "Login with username and password. return jwt token if successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(@RequestBody LoginRequest loginRequest, ServerHttpRequest request) {
        String ipAddress = getClientIp(request);
        PlatformType platformType = loginRequest.getPlatformType();

        return Mono.just(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()))
                .flatMap(authenticationManager::authenticate)// Authenticate the user with database
                .flatMap(authentication -> {
                    CustomUserDetail user = (CustomUserDetail) authentication.getPrincipal();

                    return tokenService.generateToken(user)
                            .map(authResponse -> {
                                memberService.memberActivityLog(user.getMemberId(), UserActivityType.LOGIN, platformType, ipAddress).subscribe();
                                return ResponseEntity.ok(authResponse);
                            });
                })
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).build()));
    }

    @Operation(summary = "Refresh token", description = "Refresh access token with refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @PostMapping("/refresh")
    public Mono<ResponseEntity<AuthResponse>> refreshToken(@RequestBody TokenRefreshRequest tokenRefreshRequest) {
        return tokenService.validateAndRefreshToken(tokenRefreshRequest.getRefreshToken())
                .flatMap(authResponse -> Mono.just(ResponseEntity.ok(authResponse)))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(401).build()));
    }

    @Operation(summary = "Logoff", description = "Logoff the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logoff successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    @PostMapping("/logout")
    public Mono<ResponseEntity<?>> logout() {
        // TODO: add log off service
        return Mono.just(ResponseEntity.ok(new AuthResponse(true, null, null)));
    }

    // Method to get client IP address in WebFlux
    private String getClientIp(ServerHttpRequest request) {
        return request.getHeaders().getFirst("X-Forwarded-For") != null
                ? request.getHeaders().getFirst("X-Forwarded-For")
                : request.getRemoteAddress().getAddress().getHostAddress();
    }
}
