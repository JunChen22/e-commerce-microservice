package com.itsthatjun.ecommerce.model.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("article_image")
public class ArticleImage {
    @Id
    private Integer id;

    private Integer articleId;

    private String filename;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}