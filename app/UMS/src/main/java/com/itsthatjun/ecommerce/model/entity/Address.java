package com.itsthatjun.ecommerce.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("address")
public class Address {
    @Id
    private Integer id;

    private UUID memberId;

    private String receiverName;

    private String phoneNumber;

    private String detailAddress;

    private String city;

    private String state;

    private String zipCode;

    private String note;

    private LocalDateTime updatedAt;
}