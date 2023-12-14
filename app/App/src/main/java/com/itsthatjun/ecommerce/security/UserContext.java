package com.itsthatjun.ecommerce.security;

import lombok.Getter;

@Getter
public class UserContext {

    private String name;
    private int userId;

    public UserContext(String name, int userId) {
        this.name = name;
        this.userId = userId;
    }
}
