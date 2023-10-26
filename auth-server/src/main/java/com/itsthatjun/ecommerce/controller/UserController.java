package com.itsthatjun.ecommerce.controller;


import com.itsthatjun.ecommerce.dto.LoginRequest;
import com.itsthatjun.ecommerce.dto.LoginResponse;
import com.itsthatjun.ecommerce.security.jwt.JwtTokenUtil;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(tags = "User related", description = "retrieve user information")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final MemberServiceImpl memberService;

    private final JwtTokenUtil tokenUtil;

    @Autowired
    public UserController(MemberServiceImpl memberService, JwtTokenUtil tokenUtil) {
        this.memberService = memberService;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping("/login")
    @ApiOperation(value = "Login landing page")
    public String login() {
        return "at auth login, showLoginForm:true";
    }

    @PostMapping("/login")
    @ApiOperation(value = "Login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {

        String token = memberService.login(loginRequest.getUsername(), loginRequest.getPassword());
        if (token.isEmpty()) {
            return new ResponseEntity<>(new LoginResponse(false, token), HttpStatus.UNAUTHORIZED);
        }

        return ResponseEntity.ok(new LoginResponse(true, token));
    }
}
