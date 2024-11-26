package com.itsthatjun.ecommerce.controller.admin;

import com.itsthatjun.ecommerce.dto.admin.AdminMemberDetail;
import com.itsthatjun.ecommerce.service.admin.AdminMemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/user")
@Tag(name = "User related", description = "retrieve user information")
public class AdminUserController {

    private final AdminMemberServiceImpl memberService;

    @Autowired
    public AdminUserController(AdminMemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "Get user information", description = "Get user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User information retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found")})
    @GetMapping("/getInfo")
    public Mono<AdminMemberDetail> getInfo(@RequestParam UUID memberId) {
        return memberService.getMemberDetailByMemberId(memberId);
    }
}
