package com.itsthatjun.ecommerce.security.jwt;

import com.itsthatjun.ecommerce.security.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
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
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtAuthenticationWebFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(header);
        if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
            String jwt = getJWTFromHeader(authHeader);
            String name = jwtTokenUtil.getUserNameFromToken(jwt);
            int userId = jwtTokenUtil.getUserIdFromToken(jwt);

            return Mono.justOrEmpty(jwtTokenUtil.validateToken(jwt))
                    .flatMap(isValid -> {
                        if (isValid) {
                            UserContext userContext = new UserContext(userId, name, jwt);
                            Authentication authentication = new JwtAuthenticationToken(userContext, null, jwt);
                            // Set the security context
                            return chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                        } else {
                            log.info("Invalid JWT");
                            return Mono.error(new Exception("Invalid JWT"));
                        }
                    });
        }
        return chain.filter(exchange);
    }

    private String getJWTFromHeader(String header) {
        if (!header.isEmpty() && header.startsWith(tokenPrefix)) {
            return header.substring(tokenPrefix.length()).trim();
        }
        return null;
    }
}
