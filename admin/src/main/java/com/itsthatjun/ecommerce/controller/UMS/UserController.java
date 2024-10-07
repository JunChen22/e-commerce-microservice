package com.itsthatjun.ecommerce.controller.UMS;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import com.itsthatjun.ecommerce.service.UMS.impl.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/user")
@PreAuthorize("hasRole('ROLE_admin_user')")
@Tag(name = "User related", description = "retrieve user information")
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    @ApiOperation(value = "")
    public Mono<MemberDetail> getUser(@PathVariable int userId) {
        return userService.getUser(userId);
    }

    @GetMapping("/listAllUser")
    @ApiOperation(value = "")
    public Flux<Member> listAllUser() {
        return userService.listAllUser();
    }

    @GetMapping("/getMemberLoginFrequency/{userId}")
    @ApiOperation(value = "")
    public Flux<MemberLoginLog> listAllLoginFrequency(@PathVariable int userId) {
        return userService.listAllLoginFrequency(userId);
    }

    @PostMapping("/createMember")
    @PreAuthorize("hasPermission('user:create')")
    @ApiOperation(value = "")
    public Mono<Member> createMember(@RequestBody Member member) {
        return userService.createMember(member);
    }

    @PostMapping("/updateMemberInfo")
    @PreAuthorize("hasPermission('user:update')")
    @ApiOperation(value = "")
    public Mono<Member> updateMemberInfo(@RequestBody Member member) {
        return userService.updateMemberInfo(member);
    }

    @PostMapping("/updateMemberStatus")
    @PreAuthorize("hasPermission('user:update')")
    @ApiOperation(value = "")
    public Mono<Member> updateMemberStatus(@RequestBody Member member) {
        return userService.updateMemberStatus(member);
    }

    @DeleteMapping("/delete/{memberId}")
    @PreAuthorize("hasPermission('user:delete')")
    @ApiOperation(value = "")
    public Mono<Void> delete(@PathVariable int memberId) {
        return userService.delete(memberId);
    }

    @PostMapping("/message/{memberId}")
    @ApiOperation(value = "")
    public Mono<Void> sendUserEmail(@PathVariable int memberId, @RequestBody String message) {
        return userService.sendUserEmail(memberId, message);
    }

    @PostMapping("/message/all")
    @ApiOperation(value = "")
    public Mono<Void> sendAllUserEmail(@RequestBody String message) {
        return userService.sendAllUserEmail(message);
    }
}
