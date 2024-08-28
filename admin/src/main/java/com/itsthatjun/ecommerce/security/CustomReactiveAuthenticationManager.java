package com.itsthatjun.ecommerce.security;

import com.itsthatjun.ecommerce.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final AdminServiceImpl adminService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomReactiveAuthenticationManager(AdminServiceImpl adminService, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Use reactive method to load the user details
        return adminService.findByUsername(username)
                .flatMap(userDetails -> {
                    // Decode password to compare
                    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                        return Mono.error(new BadCredentialsException("Incorrect password"));
                    }

                    // Return the authentication token
                    return Mono.just(new UsernamePasswordAuthenticationToken(
                            userDetails, null, Collections.emptyList()));  // provide empty list and will use userDetail's getAuthorities
                });
    }
}
