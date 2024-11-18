package com.itsthatjun.ecommerce.security;

import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class SecurityUtil {

    private Mono<UserContext> getUserContext() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(authentication -> authentication != null && authentication.isAuthenticated())
                .map(authentication -> (UserContext) authentication.getPrincipal())
                .switchIfEmpty(Mono.error(new IllegalStateException("UserContext or MemberId not found")));
    }

    public Mono<String> getJwtToken() {
        return getUserContext().map(UserContext::getJwtToken);
    }

    public Mono<UUID> getMemberId() {
        return getUserContext().map(UserContext::getMemberId);
    }
}