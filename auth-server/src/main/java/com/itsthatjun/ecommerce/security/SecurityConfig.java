package com.itsthatjun.ecommerce.security;

import com.itsthatjun.ecommerce.security.handler.CustomAccessDeniedHandler;
import com.itsthatjun.ecommerce.security.handler.CustomAuthenticationEntryPoint;
import com.itsthatjun.ecommerce.service.impl.MemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final MemberServiceImpl memberService;

    @Autowired
    public SecurityConfig(CustomAuthenticationEntryPoint authenticationEntryPoint, CustomAccessDeniedHandler accessDeniedHandler,
                          MemberServiceImpl userDetailsService) {
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.memberService = userDetailsService;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authenticationManager(authenticationManager())
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers(HttpMethod.POST, "/login").permitAll()
                                .pathMatchers("/actuator/**").permitAll() // docker's health check will check the health of the service
                                //.pathMatchers("/**").permitAll() // for testing purposes. All endpoints are open. Remove when needed.
                                .anyExchange().authenticated()
                ).exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                ).build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return new CustomReactiveAuthenticationManager(memberService, passwordEncoder());
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
