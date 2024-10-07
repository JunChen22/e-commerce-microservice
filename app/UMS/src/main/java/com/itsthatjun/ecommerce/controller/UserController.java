package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.security.SecurityUtil;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@Tag(name = "User related", description = "retrieve user information")
public class UserController {

    private final MemberServiceImpl memberService;

    private final SecurityUtil securityUtil;

    @Autowired
    public UserController(MemberServiceImpl memberService, SecurityUtil securityUtil) {
        this.memberService = memberService;
        this.securityUtil = securityUtil;
    }

    @GetMapping("/getInfo")
    @ApiOperation(value = "")
    public Mono<MemberDetail> getInfo() {
        return memberService.getInfo();
    }
}
