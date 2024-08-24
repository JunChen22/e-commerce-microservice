package com.itsthatjun.ecommerce.security;

import com.itsthatjun.ecommerce.security.handler.CustomAccessDeniedHandler;
import com.itsthatjun.ecommerce.security.handler.CustomAuthenticationEntryPoint;
import com.itsthatjun.ecommerce.security.jwt.JwtTokenUtil;
import com.itsthatjun.ecommerce.service.impl.AdminServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final AdminServiceImpl adminService;
    private final JwtTokenUtil jwtTokenUtil;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public SecurityConfig(AdminServiceImpl adminService, JwtTokenUtil jwtTokenUtil, CustomAuthenticationEntryPoint authenticationEntryPoint, CustomAccessDeniedHandler accessDeniedHandler) {
        this.adminService = adminService;
        this.jwtTokenUtil = jwtTokenUtil;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {


    }

    /*
    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*","/swagger-resources/**","/v2/api-docs/**", "/actuator/**").permitAll()
                .antMatchers(HttpMethod.POST,"/admin/login").permitAll()
                //.antMatchers("/**").permitAll()  // for testing purposes. All endpoints are open. Remove when needed.
                .anyRequest()
                .authenticated();

        // authenticate the JWT token before Spring Security if you have a token.
        httpSecurity.addFilterBefore(jwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        httpSecurity.exceptionHandling().accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(authenticationEntryPoint);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(adminService).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationTokenFilter() {
        return new JwtAuthenticationFilter(adminService, jwtTokenUtil);
    }
     */
}
