package com.itsthatjun.ecommerce.controller.UMS;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.dto.ums.model.AddressDTO;
import com.itsthatjun.ecommerce.service.UMS.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Get user info", description = "Get user info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get user info"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @GetMapping("/getInfo")
    public Mono<MemberDetail> getInfo() {
        return userService.getInfo();
    }

    @Operation(summary = "Register", description = "Register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Register"),
            @ApiResponse(responseCode = "404", description = "Register failed")})
    @PostMapping("/register")
    public Mono<MemberDetail> register(@RequestBody MemberDetail newMemberDetail) {
        return userService.register(newMemberDetail);
    }

    @Operation(summary = "Update password", description = "Update password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update password"),
            @ApiResponse(responseCode = "404", description = "Update password failed")})
    @PostMapping("/updatePassword")
    public Mono<Void> updatePassword(@RequestBody String newPassword) {
        return userService.updatePassword(newPassword);
    }

    @Operation(summary = "Update user info", description = "Update user info")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update user info"),
            @ApiResponse(responseCode = "404", description = "Update user info failed")})
    @PostMapping("/updateInfo")
    public Mono<MemberDetail> updateInfo(@RequestBody MemberDetail memberDetail) {
        return userService.updateInfo(memberDetail);
    }

    @Operation(summary = "Update address", description = "Update address")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Update address"),
            @ApiResponse(responseCode = "404", description = "Update address failed")})
    @PostMapping("/updateAddress")
    public Mono<AddressDTO> updateAddress(@RequestBody AddressDTO newAddress) {
        return userService.updateAddress(newAddress);
    }

    @Operation(summary = "Delete account", description = "Delete account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Delete account"),
            @ApiResponse(responseCode = "404", description = "Delete account failed")})
    @PostMapping("/deleteAccount")
    public Mono<Void> deleteAccount() {
        return userService.deleteAccount();
    }
}
