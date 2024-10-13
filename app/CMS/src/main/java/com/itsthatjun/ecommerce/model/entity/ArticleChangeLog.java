package com.itsthatjun.ecommerce.model.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data@Getter
@Setter
@Table("article_change_log")
public class ArticleChangeLog {
    @Id
    private Integer id;

    private Integer articleId;

    private String updateAction;

    private String operator;

    private LocalDateTime createdAt;
}