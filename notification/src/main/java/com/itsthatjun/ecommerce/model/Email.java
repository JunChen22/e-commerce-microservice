package com.itsthatjun.ecommerce.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Date;

@Data
@Table("email")
public class Email {
    @Id
    private Integer id;

    private String serviceType;

    private String actionType;

    private String senderEmail;

    private String recipientEmail;

    private String subject;

    private String body;

    private String operator;

    private Date createdAt;
}