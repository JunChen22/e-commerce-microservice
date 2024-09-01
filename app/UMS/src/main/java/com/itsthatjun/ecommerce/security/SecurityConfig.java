package com.itsthatjun.ecommerce.security;

import com.itsthatjun.ecommerce.security.jwt.JwtAuthenticationWebFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;

    @Autowired
    public SecurityConfig(JwtAuthenticationWebFilter jwtAuthenticationWebFilter) {
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers("/actuator/*").permitAll()
                                //.pathMatchers("/**").permitAll() // for testing purposes. All endpoints are open. Remove when needed.
                                .anyExchange().authenticated()
                ).addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
