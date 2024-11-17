package com.itsthatjun.ecommerce.model.entity;

import com.itsthatjun.ecommerce.enums.status.AccountStatus;
import com.itsthatjun.ecommerce.enums.status.LifeCycleStatus;
import com.itsthatjun.ecommerce.enums.status.VerificationStatus;
import com.itsthatjun.ecommerce.enums.type.PlatformType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("member")
public class Member {
    @Id
    private UUID id;

    private String username;

    private String password;

    private String name;

    private String phoneNumber;

    private String email;

    private Integer emailSubscription;

    private AccountStatus status;

    private VerificationStatus verifiedStatus;

    private LifeCycleStatus lifecycleStatus;

    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    private PlatformType platformType;
}