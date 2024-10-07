package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.LoginRequest;
import com.itsthatjun.ecommerce.dto.LoginResponse;
import com.itsthatjun.ecommerce.security.CustomReactiveAuthenticationManager;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.security.jwt.JwtTokenUtil;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "User related", description = "retrieve user information")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final CustomReactiveAuthenticationManager authenticationManager;
    private final MemberServiceImpl memberService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserController(CustomReactiveAuthenticationManager authenticationManager, MemberServiceImpl memberService,
                          JwtTokenUtil jwtTokenUtil) {
        this.authenticationManager = authenticationManager;
        this.memberService = memberService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/login")
    @ApiOperation("Login")
    public Mono<ResponseEntity<?>> login(@RequestBody LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            ).flatMap(authentication -> {
                CustomUserDetail user = (CustomUserDetail) authentication.getPrincipal();
                String token = jwtTokenUtil.generateToken(user);
                memberService.memberLoginLog(user.getUserId());
                return Mono.just(ResponseEntity.ok(new LoginResponse(true, token)));
            });
    }
}
