package com.itsthatjun.ecommerce.model;

import com.itsthatjun.ecommerce.enums.status.AccountStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Table("member")
public class Member {
    @Id
    private Integer id;

    private String username;

    private String password;

    private String name;

    private String phoneNumber;

    private String email;

    private Integer emailSubscription;

    private AccountStatus status;

    private Integer verifiedStatus;

    private Integer deleteStatus;

    private LocalDateTime createdAt;

    private LocalDateTime lastLogin;

    private String sourceType;
}