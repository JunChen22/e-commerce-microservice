package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.LoginRequest;
import com.itsthatjun.ecommerce.dto.LoginResponse;
import com.itsthatjun.ecommerce.security.CustomReactiveAuthenticationManager;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.security.jwt.JwtTokenUtil;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Api(tags = "User related", description = "retrieve user information")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final CustomReactiveAuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserController(CustomReactiveAuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/login")
    @ApiOperation("Login")
    public Mono<ResponseEntity<?>> login(@RequestBody LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            ).flatMap(authentication -> {
                String token = jwtTokenUtil.generateToken((CustomUserDetail) authentication.getPrincipal());
                return Mono.just(ResponseEntity.ok(new LoginResponse(true, token)));
            });
    }
}
