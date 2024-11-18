package com.itsthatjun.ecommerce.security;

import com.itsthatjun.ecommerce.security.handler.CustomAccessDeniedHandler;
import com.itsthatjun.ecommerce.security.handler.CustomAuthenticationEntryPoint;
import com.itsthatjun.ecommerce.security.jwt.JwtAuthenticationWebFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final JwtAuthenticationWebFilter jwtAuthenticationWebFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public SecurityConfig(JwtAuthenticationWebFilter jwtAuthenticationWebFilter, CustomAuthenticationEntryPoint authenticationEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler) {
        this.jwtAuthenticationWebFilter = jwtAuthenticationWebFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .authorizeExchange(exchanges ->
                        exchanges
                                .pathMatchers(HttpMethod.GET, "/article/listAll", "/article/{slug}").permitAll()
                                .pathMatchers(HttpMethod.GET, "/brand/listAll", "/brand/list", "/brand/product/{brandId}", "/brand/{brandId}").permitAll()
                                .pathMatchers(HttpMethod.GET, "/product/listAll", "/product/{id}", "/product/list").permitAll()
                                .pathMatchers(HttpMethod.GET, "/review/detail/{reviewId}", "/review/getAllProductReview/**").permitAll()
                                .pathMatchers(HttpMethod.GET, "/sale/AllPromotionSale", "/sale/AllPromotionSaleItem", "/sale/AllFlashSaleItem").permitAll()
                                .pathMatchers(HttpMethod.GET,"/order/payment/**").permitAll() // remove it when you have frontend that store the jwt
                                .pathMatchers("/swagger-ui.html",
                                        "/swagger-resources/**",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/swagger**",
                                        "/v2/api**",
                                        "/webjars/**").permitAll()
                                .pathMatchers("/").permitAll()
                                //.pathMatchers("/**").permitAll() // for testing purposes. All endpoints are open. Remove when needed.
                                .anyExchange().authenticated()
                ).exceptionHandling(exceptionHandlingSpec -> exceptionHandlingSpec
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler)
                ).addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
