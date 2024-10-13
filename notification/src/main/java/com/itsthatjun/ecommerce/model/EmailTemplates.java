package com.itsthatjun.ecommerce.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Getter
@Setter
@Table("email_templates")
public class EmailTemplates {
    @Id
    private Integer id;

    private String serviceName;

    private String templateText;

    private Date createdAt;

    private Date updatedAt;
}