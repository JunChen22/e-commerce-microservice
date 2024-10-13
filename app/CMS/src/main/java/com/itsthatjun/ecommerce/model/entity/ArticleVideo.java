package com.itsthatjun.ecommerce.model.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Setter
@Table("article_video")
public class ArticleVideo {
    @Id
    private Integer id;

    private Integer articleId;

    private String url;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}