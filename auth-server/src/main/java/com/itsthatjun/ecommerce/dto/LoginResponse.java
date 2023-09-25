package com.itsthatjun.ecommerce.dto;

import lombok.Getter;

@Getter
public class LoginResponse {

    private boolean success;
    private String token;

    public LoginResponse(boolean success, String token) {
        this.success = success;
        this.token = token;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "success=" + success +
                ", token='" + token + '\'' +
                '}';
    }
}
