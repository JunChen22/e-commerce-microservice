package com.itsthatjun.ecommerce.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String username;
    private final List<GrantedAuthority> authorities;

    public JwtAuthenticationToken(String username, List<GrantedAuthority> authorities) {
        super(authorities);
        this.username = username;
        this.authorities = authorities;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
