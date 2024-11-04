package com.itsthatjun.ecommerce.mbg.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
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

    private LocalDateTime createdAt;
}