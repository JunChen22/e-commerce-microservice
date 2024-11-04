package com.itsthatjun.ecommerce.model.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("review_update_log")
public class ReviewUpdateLog {
    @Id
    private Integer id;

    private Integer reviewId;

    private String updateAction;

    private String operator;

    private LocalDateTime createdAt;
}