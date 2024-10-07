package com.itsthatjun.ecommerce.controller.UMS;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.dto.ums.model.AddressDTO;
import com.itsthatjun.ecommerce.service.UMS.impl.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Tag(name = "User controller", description = "user controller")
@RequestMapping("/user")
public class UserAggregate {

    private final UserServiceImpl userService;

    @Autowired
    public UserAggregate(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/getInfo")
    @ApiOperation(value = "")
    public Mono<MemberDetail> getInfo() {
        return userService.getInfo();
    }

    @PostMapping("/register")
    @ApiOperation(value = "Register")
    public Mono<MemberDetail> register(@RequestBody MemberDetail newMemberDetail) {
        return userService.register(newMemberDetail);
    }

    @PostMapping("/updatePassword")
    @ApiOperation(value = "")
    public Mono<String> updatePassword(@RequestBody String newPassword) {
        return userService.updatePassword(newPassword);
    }

    @PostMapping("/updateInfo")
    @ApiOperation(value = "password, name, icon")
    public Mono<MemberDetail> updateInfo(@RequestBody MemberDetail memberDetail) {
        return userService.updateInfo(memberDetail);
    }

    @PostMapping("/updateAddress")
    @ApiOperation(value = "")
    public Mono<AddressDTO> updateAddress(@RequestBody AddressDTO newAddress) {
        return userService.updateAddress(newAddress);
    }

    @PostMapping("/deleteAccount")
    @ApiOperation(value = "")
    public Mono<Void> deleteAccount() {
        return userService.deleteAccount();
    }
}
