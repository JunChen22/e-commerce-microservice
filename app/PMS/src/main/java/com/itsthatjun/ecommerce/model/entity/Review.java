package com.itsthatjun.ecommerce.model.entity;

import com.itsthatjun.ecommerce.enums.status.VerificationStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("review")
public class Review {
    @Id
    private Integer id;

    private Integer productId;

    private Integer memberId;

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