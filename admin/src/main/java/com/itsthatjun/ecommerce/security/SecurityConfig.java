package com.itsthatjun.ecommerce.security;

import com.itsthatjun.ecommerce.security.handler.CustomAccessDeniedHandler;
import com.itsthatjun.ecommerce.security.handler.CustomAuthenticationEntryPoint;
import com.itsthatjun.ecommerce.security.jwt.JwtAuthenticationWebFilter;
import com.itsthatjun.ecommerce.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final AdminServiceImpl adminService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(JwtAuthenticationWebFilter jwtAuthenticationWebFilter, CustomAuthenticationEntryPoint authenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler, AdminServiceImpl adminService, BCryptPasswordEncoder passwordEncoder) {
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
        this.adminService = adminService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authenticationManager(authenticationManager())
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers("/", "/index", "/css/*", "/js/*", "/swagger-resources/**", "/v2/api-docs/**", "/actuator/**").permitAll()
                                .pathMatchers(HttpMethod.POST, "/admin/login").permitAll()
                                //.pathMatchers("/**").permitAll() // for testing purposes. All endpoints are open. Remove when needed.
                                .anyExchange().authenticated()
                ).exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                ).addFilterBefore(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return new CustomReactiveAuthenticationManager(adminService, passwordEncoder);
    }
}
