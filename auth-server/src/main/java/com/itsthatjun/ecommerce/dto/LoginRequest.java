package com.itsthatjun.ecommerce.dto;

import lombok.Getter;

@Getter
public class LoginRequest {

    //@NotEmpty(message = "username can not be empty")
    String username;

    //@NotEmpty(message = "password can not be empty")
    String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
