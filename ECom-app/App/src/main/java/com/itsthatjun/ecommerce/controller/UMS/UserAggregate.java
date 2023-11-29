package com.itsthatjun.ecommerce.controller.UMS;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.dto.ums.model.AddressDTO;
import com.itsthatjun.ecommerce.security.UserContext;
import com.itsthatjun.ecommerce.service.UMS.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Api(tags = "User controller", description = "user controller")
@RequestMapping("/user")
public class UserAggregate {

    private static final Logger LOG = LoggerFactory.getLogger(UserAggregate.class);

    private final UserServiceImpl userService;

    @Autowired
    public UserAggregate(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/getInfo")
    @ApiOperation(value = "")
    public Mono<MemberDetail> getInfo() {
        int userId = getUserId();
        return userService.getInfo(userId);
    }

    @PostMapping("/register")
    @ApiOperation(value = "Register")
    public Mono<MemberDetail> register(@RequestBody MemberDetail newMemberDetail) {
        return userService.register(newMemberDetail);
    }

    @PostMapping("/updatePassword")
    @ApiOperation(value = "")
    public Mono<String> updatePassword(@RequestBody String newPassword) {
        int userId = getUserId();
        return userService.updatePassword(newPassword, userId);
    }

    @PostMapping("/updateInfo")
    @ApiOperation(value = "password, name, icon")
    public Mono<MemberDetail> updateInfo(@RequestBody MemberDetail memberDetail) {
        int userId = getUserId();
        return userService.updateInfo(memberDetail, userId);
    }

    @PostMapping("/updateAddress")
    @ApiOperation(value = "")
    public Mono<AddressDTO> updateAddress(@RequestBody AddressDTO newAddress) {
        int userId = getUserId();
        return userService.updateAddress(newAddress, userId);
    }

    @PostMapping("/deleteAccount")
    @ApiOperation(value = "")
    public Mono<Void> deleteAccount() {
        int userId = getUserId();
        return userService.deleteAccount(userId);
    }

    private int getUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext userContext = (UserContext) authentication.getPrincipal();
        int userId = userContext.getUserId();
        return userId;
    }
}
