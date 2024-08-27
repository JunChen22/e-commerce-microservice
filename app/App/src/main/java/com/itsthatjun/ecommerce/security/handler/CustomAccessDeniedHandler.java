package com.itsthatjun.ecommerce.security.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAccessDeniedHandler implements ServerAccessDeniedHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, AccessDeniedException e) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.FORBIDDEN);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(
                "You don't have required role to perform this action".getBytes(StandardCharsets.UTF_8)
        ))).then(Mono.fromRunnable(() -> {
            try {
                response.getHeaders().setLocation(new URI("http://app/"));
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }));
    }
}
