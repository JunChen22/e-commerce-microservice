package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.dto.UserInfo;
import com.itsthatjun.ecommerce.security.SecurityUtil;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "User related", description = "retrieve user information")
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
        System.out.println("======================================================");
        System.out.println("UMS TOKEN : " + securityUtil.getJwtToken());
        return memberService.getInfo();
    }

    // TODO: change it to flux
    @GetMapping("/getAllUserInfo") // TODO: change it to mTLS. Currently is dangerously wide open.
    public List<UserInfo> getAllUserInfo() {
        return memberService.getAllUserInfo();
    }
}
