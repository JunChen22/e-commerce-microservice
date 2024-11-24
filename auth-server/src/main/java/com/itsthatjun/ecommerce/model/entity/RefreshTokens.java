package com.itsthatjun.ecommerce.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("refresh_tokens")
public class RefreshTokens {
    @Id
    private Integer id;

    private String refreshToken;

    private UUID memberId;

    private LocalDateTime expiryDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}