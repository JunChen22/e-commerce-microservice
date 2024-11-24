package com.itsthatjun.ecommerce.dto;

import com.itsthatjun.ecommerce.enums.type.PlatformType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public class TokenRefreshRequest implements Serializable {

    private final String refreshToken;

    private final PlatformType platformType;
}
