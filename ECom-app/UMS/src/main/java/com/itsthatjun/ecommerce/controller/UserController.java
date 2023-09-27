package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dto.MemberDetail;
import com.itsthatjun.ecommerce.mbg.model.Address;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
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

    @PostMapping("/register")
    @ApiOperation(value = "Register")
    public Mono<MemberDetail> register(@RequestBody MemberDetail memberDetail){
        return memberService.register(memberDetail);
    }

    @PostMapping("/updatePassword")
    @ApiOperation(value = "")
    Mono<Member> updatePassword(@RequestHeader("X-UserId")int userId, @RequestBody String newPassword) {
        return memberService.updatePassword(userId, newPassword);
    }

    @PostMapping("/updateInfo")
    @ApiOperation(value = "password, name, icon")
    Mono<Member> updateInfo(@RequestBody MemberDetail memberDetail) {
        return memberService.updateInfo(memberDetail);
    }

    @PostMapping("/updateAddress")
    @ApiOperation(value = "")
    Mono<Address> updateAddress(@RequestHeader("X-UserId")int userId, @RequestBody Address newAddress) {
        return memberService.updateAddress(userId, newAddress);
    }

    @PostMapping("/deleteAccount")
    @ApiOperation(value = "")
    Mono<Void> deleteAccount(@RequestHeader("X-UserId") int userId) {
        return memberService.deleteAccount(userId);
    }

    @GetMapping("/admin/getAllUser")
    @ApiOperation(value = "")
    Flux<Member> getAllUser() {
        return memberService.getAllUser();
    }

    @GetMapping("/admin/getMemberDetailByUserId/{userId}")
    @ApiOperation(value = "")
    Mono<MemberDetail> getMemberDetailByUserId(@PathVariable int userId) {
        return memberService.getMemberDetailByUserId(userId);
    }

    @GetMapping("/admin/getMemberLoginFrequency/{userId}")
    @ApiOperation(value = "")
    Flux<MemberLoginLog> getMemberLoginFrequency(@PathVariable int userId) {
        return memberService.getMemberLoginFrequency(userId);
    }

    @PostMapping("/admin/createMember")
    @ApiOperation(value = "")
    Mono<Member> createMember(@RequestBody Member newMember) {
        return memberService.createMember(newMember);
    }

    @PostMapping("/admin/updateMemberInfo")
    @ApiOperation(value = "")
    Mono<Member> updateMemberInfo(@RequestBody Member updatedMember) {
        return memberService.updateMemberInfo(updatedMember);
    }

    @PostMapping("/admin/updateMemberStatus")
    @ApiOperation(value = "")
    Mono<Member> updateMemberStatus(@RequestBody Member updatedMember) {
        return memberService.updateMemberStatus(updatedMember);
    }

    @PostMapping("/admin/deleteMember/{userId}")
    @ApiOperation(value = "")
    Mono<Member> deleteMember(@PathVariable int userId) {
        return memberService.deleteMember(userId);
    }
}
