package com.itsthatjun.ecommerce.controller;

import com.itsthatjun.ecommerce.dao.domainmodel.AdminDetail;
import com.itsthatjun.ecommerce.security.CustomReactiveAuthenticationManager;
import com.itsthatjun.ecommerce.security.CustomUserDetail;
import com.itsthatjun.ecommerce.security.jwt.JwtTokenUtil;
import com.itsthatjun.ecommerce.service.impl.AdminServiceImpl;
import com.itsthatjun.ecommerce.security.LoginRequest;
import com.itsthatjun.ecommerce.security.LoginResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
@Api(tags = {"Admin related"}, description = "admin related")
public class AdminController {

    private final AdminServiceImpl adminService;

    private final CustomReactiveAuthenticationManager authenticationManager;

    private final JwtTokenUtil jwtTokenUtil;

    // TODO: add redis to store admin name,  "Admin: " + token : adminName

    @Autowired
    public AdminController(AdminServiceImpl adminService, CustomReactiveAuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.adminService = adminService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @PostMapping("/login")
    @ApiOperation("")
    public Mono<ResponseEntity<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        // two approach, token = stateless for microservices,
        // stateful approach is with session for monolith application.
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            ).map( auth -> {
                CustomUserDetail user = (CustomUserDetail) auth.getPrincipal();
                String token = jwtTokenUtil.generateToken(user);
                return ResponseEntity.ok(new LoginResponse(true, token));
            }).onErrorResume(BadCredentialsException.class, e ->
                Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
            );
    }

    //@PreAuthorize("hasAuthority('user:read')")
    @PreAuthorize("hasRole('ROLE_admin-root')")
    @GetMapping("/roles/{id}")
    @ApiOperation("")
    public Mono<AdminDetail> getRole(@PathVariable int id) {
        return adminService.getAdminDetail(id);
    }

    // TODO:update other admins role or permission or disable account
}
