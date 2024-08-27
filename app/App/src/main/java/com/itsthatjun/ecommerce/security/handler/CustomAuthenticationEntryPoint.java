package com.itsthatjun.ecommerce.security.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException e) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.TEXT_PLAIN);
        response.setStatusCode(HttpStatus.UNAUTHORIZED);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(
                "Unauthorized, you need to login first in order to perform this action.".getBytes(StandardCharsets.UTF_8)
        ))).then(Mono.fromRunnable(() -> {
            try {
                response.getHeaders().setLocation(new URI("http://auth-server/login"));
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }));
    }
}
