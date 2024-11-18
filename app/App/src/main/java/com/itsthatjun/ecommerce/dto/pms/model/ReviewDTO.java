package com.itsthatjun.ecommerce.dto.pms.model;

import com.itsthatjun.ecommerce.enums.status.VerificationStatus;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class ReviewDTO {

    private String skuCode;

    private UUID memberId;

    private String memberName;

    private String memberIcon;

    private Integer star;

    private String tittle;

    private BigDecimal likes;

    private VerificationStatus verifyStatus;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
