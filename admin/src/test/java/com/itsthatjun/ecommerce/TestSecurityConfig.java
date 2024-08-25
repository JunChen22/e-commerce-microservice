package com.itsthatjun.ecommerce;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

@EnableWebFluxSecurity
@TestConfiguration
public class TestSecurityConfig  {

    /*
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/**").permitAll();
    }
     */
}
