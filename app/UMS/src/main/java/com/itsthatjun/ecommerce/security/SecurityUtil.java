package com.itsthatjun.ecommerce.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SecurityUtil {

    public Mono<UserContext> getUserContext() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(authentication -> {
                    if (authentication != null && authentication.getPrincipal() instanceof UserContext) {
                        return (UserContext) authentication.getPrincipal();
                    }
                    throw new IllegalStateException("UserContext not found in SecurityContext");
                });
    }

    public Mono<String> getJwtToken() {
        return getUserContext().map(UserContext::getJwtToken);
    }

    public Mono<Integer> getUserId() {
        return getUserContext().map(UserContext::getUserId);
    }
}