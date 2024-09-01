package com.itsthatjun.ecommerce.security;

import lombok.Getter;

@Getter
public class UserContext {

    private final int userId;
    private final String name;
    private final String jwtToken;
    // List<Authority> authorities; TODO: Implement Authority

    public UserContext(int userId, String name, String jwtToken) {
        this.userId = userId;
        this.name = name;
        this.jwtToken = jwtToken;
    }
}
