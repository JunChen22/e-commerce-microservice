package com.itsthatjun.ecommerce.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public JwtAuthenticationProvider(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Assume that authentication.getCredentials() contains the JWT token as a String
        String jwtToken = (String) authentication.getCredentials();

        // Parse the JWT token and extract user-related information (e.g., username, roles)
        String username = jwtTokenUtil.getNameFromToken(jwtToken);

        //List<String> roles = extractRolesFromToken(jwtToken);

        // Create a UserDetails object with the extracted information
        //Collection<GrantedAuthority> authorities = new ArrayList<>();
       // for (String role : roles) {
        //    authorities.add(new SimpleGrantedAuthority(role));
       // }

        // Create an authenticated Authentication object
        return new UsernamePasswordAuthenticationToken(username, jwtToken);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
