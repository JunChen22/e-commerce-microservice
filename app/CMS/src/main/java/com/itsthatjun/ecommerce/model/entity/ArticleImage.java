package com.itsthatjun.ecommerce.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Table("article_image")
public class ArticleImage {
    @Id
    private Integer id;

    private Integer articleId;

    private String filename;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}