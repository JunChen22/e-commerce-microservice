package com.itsthatjun.ecommerce.dto.model;

import com.itsthatjun.ecommerce.enums.status.VerificationStatus;
import com.itsthatjun.ecommerce.enums.type.PlatformType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class MemberDTO {

    private final UUID id;

    private final String username;

    private final String password;

    private final String name;

    private final String phoneNumber;

    private final String email;

    private final Integer emailSubscription;

    private final VerificationStatus verifiedStatus;

    private final LocalDateTime createdAt;

    private final LocalDateTime lastLogin;

    private final PlatformType platformType;
}
