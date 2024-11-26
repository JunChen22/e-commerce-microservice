package com.itsthatjun.ecommerce.model.entity;

import com.itsthatjun.ecommerce.enums.type.UpdateActionType;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("member_change_log")
public class MemberChangeLog {
    @Id
    private Integer id;

    private UUID memberId;

    private UpdateActionType updateAction;

    private String description;

    private String operator;

    private LocalDateTime createdAt;
}