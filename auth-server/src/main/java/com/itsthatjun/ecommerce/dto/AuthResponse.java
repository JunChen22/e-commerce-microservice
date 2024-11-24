package com.itsthatjun.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class AuthResponse implements Serializable {

    private final boolean success;

    private final String accessToken;

    private final String refreshToken;

    @Override
    public String toString() {
        return "AuthResponse{" +
                "success=" + success +
                ", accessToken='" + accessToken + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
