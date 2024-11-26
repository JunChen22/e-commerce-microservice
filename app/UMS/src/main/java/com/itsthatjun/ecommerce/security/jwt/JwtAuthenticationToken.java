package com.itsthatjun.ecommerce.security.jwt;

import com.itsthatjun.ecommerce.security.UserContext;
import lombok.Getter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private final UserContext userContext;

    private final List<GrantedAuthority> authorities;

    private final String token;

    public JwtAuthenticationToken(UserContext userContext, List<GrantedAuthority> authorities, String token) {
        super(userContext, token, authorities);
        this.userContext = userContext;
        this.authorities = authorities;
        this.token = token;
    }
}
