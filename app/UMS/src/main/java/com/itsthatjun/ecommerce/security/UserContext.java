package com.itsthatjun.ecommerce.security;

import lombok.Getter;

import java.util.UUID;

@Getter
public class UserContext {

    private final UUID memberId;

    private final String name;

    private final String jwtToken;

    // List<Authority> authorities; TODO: Implement Authority

    public UserContext(UUID memberId, String name, String jwtToken) {
        this.memberId = memberId;
        this.name = name;
        this.jwtToken = jwtToken;
    }
}
