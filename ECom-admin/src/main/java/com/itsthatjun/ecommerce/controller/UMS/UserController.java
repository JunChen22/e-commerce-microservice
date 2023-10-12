package com.itsthatjun.ecommerce.controller.UMS;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import com.itsthatjun.ecommerce.dto.ums.event.UmsAdminUserEvent;
import com.itsthatjun.ecommerce.mbg.model.Member;
import com.itsthatjun.ecommerce.mbg.model.MemberLoginLog;
import com.itsthatjun.ecommerce.service.UMS.impl.UserServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;

import javax.servlet.http.HttpSession;

import static com.itsthatjun.ecommerce.dto.ums.event.UmsAdminUserEvent.Type.*;
import static java.util.logging.Level.FINE;

@RestController
@RequestMapping("/user")
@Api(tags = "User related", description = "retrieve user information")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

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

    @GetMapping("/getAllUser")
    @ApiOperation(value = "")
    public Flux<Member> getAllUser() {
        return userService.getAllUser();
    }

    @GetMapping("/getMemberLoginFrequency/{userId}")
    @ApiOperation(value = "")
    public Flux<MemberLoginLog> listAllLoginFrequency(@PathVariable int userId) {
        return userService.listAllLoginFrequency(userId);
    }

    @PostMapping("/createMember")
    @ApiOperation(value = "")
    public Mono<Member> createMember(@RequestBody Member member, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        return userService.createMember(member, operatorName);
    }

    @PostMapping("/updateMemberInfo")
    @ApiOperation(value = "")
    public Mono<Member> updateMemberInfo(@RequestBody Member member, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        return userService.updateMemberInfo(member, operatorName);
    }

    @PostMapping("/updateMemberStatus")
    @ApiOperation(value = "")
    public Mono<Member> updateMemberStatus(@RequestBody Member member, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        return userService.updateMemberStatus(member, operatorName);
    }

    @DeleteMapping("/delete/{memberId}")
    @ApiOperation(value = "")
    public Mono<Void> delete(@PathVariable int memberId, HttpSession session) {
        String operatorName  = (String) session.getAttribute("adminName");
        return userService.delete(memberId, operatorName);
    }
}
