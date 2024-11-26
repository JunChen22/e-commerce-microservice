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

import java.util.UUID;

@Component
public class JwtAuthenticationWebFilter implements WebFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationWebFilter.class);

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.HEADER_STRING}")
    private String header;

    @Value("${jwt.tokenPrefix}")
    private String tokenPrefix;

    @Autowired
    public JwtAuthenticationWebFilter(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst(header);
        if (authHeader != null && authHeader.startsWith(tokenPrefix)) {
            String jwt = getJWTFromHeader(authHeader);
            return jwtTokenUtil.validateToken(jwt)
                    .flatMap(isValid -> {
                        if (isValid) {
                            return Mono.zip(
                                    jwtTokenUtil.getUserNameFromToken(jwt),
                                    jwtTokenUtil.getMemberIdFromToken(jwt)
                            ).flatMap(tuple -> {
                                String name = tuple.getT1();
                                UUID memberId = tuple.getT2();

                                UserContext userContext = new UserContext(memberId, name, jwt);
                                Authentication authentication = new JwtAuthenticationToken(userContext, null, jwt);

                                // Set the security context
                                return chain.filter(exchange)
                                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication));
                            });
                        } else {
                            LOG.info("Invalid JWT");
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
