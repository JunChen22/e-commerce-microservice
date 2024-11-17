package com.itsthatjun.ecommerce.dto;

import lombok.Getter;

@Getter
public class LoginRequest {

    private final String username;

    private final String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
