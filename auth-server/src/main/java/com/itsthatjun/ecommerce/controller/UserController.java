package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.LoginRequest;
import com.itsthatjun.ecommerce.dto.LoginResponse;
import com.itsthatjun.ecommerce.repository.MemberRepository;
import com.itsthatjun.ecommerce.security.CustomReactiveAuthenticationManager;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.security.jwt.JwtTokenUtil;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "User related", description = "retrieve user information")
public class UserController {

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

    @Autowired
    private MemberRepository memberRepository;

    @Operation(summary = "Login",
            description = "Login with username and password. return jwt token if successful")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public Mono<ResponseEntity<?>> login(@RequestBody LoginRequest loginRequest) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            ).flatMap(authentication -> {
                CustomUserDetail user = (CustomUserDetail) authentication.getPrincipal();
                String token = jwtTokenUtil.generateToken(user);
                memberService.memberLoginLog(user.getMemberId());
                return Mono.just(ResponseEntity.ok(new LoginResponse(true, token)));
            });
    }

    @Operation(summary = "Logoff", description = "Logoff the user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Logoff successful"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping(value = "/logout", consumes = "application/json", produces = "application/json")
    public Mono<ResponseEntity<?>> logout() {
        return Mono.just(ResponseEntity.ok(new LoginResponse(true, "Logoff successful")));
    }
}
