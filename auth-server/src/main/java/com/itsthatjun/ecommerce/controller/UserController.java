package com.itsthatjun.ecommerce.controller;


import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/user")
@Api(tags = "User related", description = "retrieve user information")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final MemberServiceImpl memberService;

    @Autowired
    public UserController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    @ApiOperation(value = "at login")
    public String login() {
        return "at auth login";
    }
}
