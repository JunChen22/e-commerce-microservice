package com.itsthatjun.ecommerce.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@Table("email_templates")
public class EmailTemplates {
    @Id
    private Integer id;

    private String serviceName;

    private String templateText;

    private Date createdAt;

    private Date updatedAt;
}