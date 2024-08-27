package com.itsthatjun.ecommerce.security;

import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class CustomReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final MemberServiceImpl memberService;
    private final PasswordEncoder passwordEncoder;

    public CustomReactiveAuthenticationManager(MemberServiceImpl memberService, PasswordEncoder passwordEncoder) {
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        // Use reactive method to load the user details
        return memberService.findByUsername(username)
                .flatMap(userDetails -> {
                    // Decode password to compare
                    if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                        return Mono.error(new BadCredentialsException("Incorrect password"));
                    }

                    // Return the authentication token
                    return Mono.just(new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()));

                });
    }
}