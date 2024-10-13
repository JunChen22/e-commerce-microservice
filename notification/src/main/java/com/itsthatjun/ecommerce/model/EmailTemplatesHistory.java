package com.itsthatjun.ecommerce.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Getter
@Setter
@Table("email_templates_history")
public class EmailTemplatesHistory {
    @Id
    private Integer id;

    private Integer templateId;

    private String updateAction;

    private Date createdAt;

    private String operator;
}