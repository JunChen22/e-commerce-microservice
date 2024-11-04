package com.itsthatjun.ecommerce.mbg.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("email_templates_history")
public class EmailTemplatesHistory {
    @Id
    private Integer id;

    private Integer templateId;

    private String updateAction;

    private LocalDateTime createdAt;

    private String operator;
}