package com.itsthatjun.ecommerce.model.entity;

import com.itsthatjun.ecommerce.enums.type.PlatformType;
import com.itsthatjun.ecommerce.enums.type.UserActivityType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("member_activity_log")
public class MemberActivityLog {
    @Id
    private Integer id;

    private UUID memberId;

    private UserActivityType activity;

    private PlatformType platformType;

    private String ipAddress;

    private LocalDateTime createdAt;
}