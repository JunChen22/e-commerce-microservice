package com.itsthatjun.ecommerce.security.jwt;

import com.itsthatjun.ecommerce.service.impl.AdminServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationWebFilter implements WebFilter {

    @Value("${jwt.HEADER_STRING}")
    private String header;
    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    private final Logger log = LoggerFactory.getLogger(JwtAuthenticationWebFilter.class);

    private final AdminServiceImpl adminService;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtAuthenticationWebFilter(AdminServiceImpl adminService, JwtTokenUtil jwtTokenUtil) {
        this.adminService = adminService;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(header);
        if (authHeader != null) {
            log.info("Auth Header: {}", authHeader);
        } else {
            log.info("No Auth Header found");
        }

        if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
            String jwt = getJWTFromHeader(authHeader);
            String username = jwtTokenUtil.getUsernameFromToken(jwt);
            log.info("JWT: {}, Username: {}", jwt, username);
            return Mono.justOrEmpty(adminService.loadUserByUsername(username))
                    .filter(userDetails -> jwtTokenUtil.validateToken(jwt, userDetails))
                    .flatMap(userdetails -> {
                        log.info("JWT validated for user: {}", userdetails.getUsername());
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userdetails, null, userdetails.getAuthorities());
                        return chain.filter(exchange)
                                .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                    });
        }
        log.info("Proceeding without authentication");
        return chain.filter(exchange);
    }

    private String getJWTFromHeader(String header) {
        if (!header.isEmpty() && header.startsWith(tokenPrefix)) {
            String bearerToken = header.substring(tokenPrefix.length()).trim();
            return bearerToken;
        }
        return null;
    }
}
