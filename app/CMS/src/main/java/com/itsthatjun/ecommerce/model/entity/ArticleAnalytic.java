package com.itsthatjun.ecommerce.model.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("article_analytic")
public class ArticleAnalytic {
    @Id
    private Integer id;

    private Integer articleId;

    private LocalDateTime hour;

    private Integer viewCount;

    private Integer likeCount;

    private Integer shareCount;

    private Integer commentCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}