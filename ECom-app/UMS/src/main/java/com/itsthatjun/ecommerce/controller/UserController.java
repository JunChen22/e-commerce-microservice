package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@Api(tags = "User related", description = "retrieve user information")
public class UserController {

    private final MemberServiceImpl memberService;

    @Autowired
    public UserController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/getInfo")
    @ApiOperation(value = "")
    Mono<MemberDetail> getInfo(@RequestHeader("X-UserId")int userId) {
        return memberService.getInfo(userId);
    }
}
