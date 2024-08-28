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

        if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
            String jwt = getJWTFromHeader(authHeader);
            String username = jwtTokenUtil.getUsernameFromToken(jwt);
            log.info("JWT: {}, Username: {}", jwt, username);

            return adminService.findByUsername(username)
                    .filter(userDetails -> jwtTokenUtil.validateToken(jwt, userDetails))
                    .map(userDetails -> {
                        log.info("JWT validated for user: {}", userDetails.getUsername());
                        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    })
                    .flatMap(authentication ->
                            chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                    )
                    .switchIfEmpty(chain.filter(exchange));
        }

        log.info("Proceeding without authentication");
        return chain.filter(exchange);
    }

    private String getJWTFromHeader(String header) {
        if (!header.isEmpty() && header.startsWith(tokenPrefix)) {
            return header.substring(tokenPrefix.length()).trim();
        }
        return null;
    }
}
