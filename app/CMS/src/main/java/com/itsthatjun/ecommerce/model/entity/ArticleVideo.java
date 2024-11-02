package com.itsthatjun.ecommerce.model.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

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