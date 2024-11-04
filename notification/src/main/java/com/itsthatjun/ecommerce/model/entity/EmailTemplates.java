package com.itsthatjun.ecommerce.model.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("email_templates")
public class EmailTemplates {
    @Id
    private Integer id;

    private String serviceName;

    private String templateText;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}