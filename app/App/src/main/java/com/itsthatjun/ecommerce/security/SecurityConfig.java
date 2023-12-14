package com.itsthatjun.ecommerce.security;

import com.itsthatjun.ecommerce.security.jwt.JwtAuthenticationFilter;
import com.itsthatjun.ecommerce.security.jwt.JwtAuthenticationProvider;
import com.itsthatjun.ecommerce.security.jwt.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    private final JwtTokenUtil jwtTokenUtil;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    public SecurityConfig(CustomAuthenticationEntryPoint customAuthenticationEntryPoint, JwtTokenUtil jwtTokenUtil,
                          CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
        this.jwtTokenUtil = jwtTokenUtil;
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/article/all", "/article/{articleId}").permitAll()
                .antMatchers(HttpMethod.GET, "/brand/listAll", "/brand/list", "/brand/product/{brandId}", "/brand/{brandId}").permitAll()
                .antMatchers(HttpMethod.GET, "/product/listAll", "/product/{id}", "/product/list").permitAll()
                .antMatchers(HttpMethod.GET, "/review/detail/{reviewId}", "/review/getAllProductReview/**").permitAll()
                .antMatchers(HttpMethod.GET, "/sale/AllPromotionSale", "/sale/AllPromotionSaleItem", "/sale/AllFlashSaleItem").permitAll()
                .antMatchers(HttpMethod.GET,"/order/payment/**").permitAll() // remove it when you have frontend that store the jwt
                .antMatchers("/swagger-ui.html",
                        "/swagger-resources/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/swagger**",
                        "/v2/api**",
                        "/webjars/**").permitAll()
                .antMatchers("/").permitAll()
                .anyRequest()
                .authenticated();

        httpSecurity.authenticationProvider(jwtAuthenticationProvider());

        // authenticate the JWT token before Spring Security if you have a token.
        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        httpSecurity.exceptionHandling().authenticationEntryPoint(customAuthenticationEntryPoint);

        httpSecurity.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationFilter(jwtTokenUtil);
    }

    @Bean
    public AuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider(jwtTokenUtil);
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Allow requests from all origins, you may want to restrict this in a production environment
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("GET");
        config.addAllowedMethod("POST");
        config.addAllowedMethod("PUT");
        config.addAllowedMethod("DELETE");

        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
