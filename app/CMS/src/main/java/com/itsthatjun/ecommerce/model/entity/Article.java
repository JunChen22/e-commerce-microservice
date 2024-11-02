package com.itsthatjun.ecommerce.model.entity;

import com.itsthatjun.ecommerce.enums.status.PublishStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("article")
public class Article {
    @Id
    private Integer id;

    private String title;

    private String slug;

    private Integer authorId;

    private String authorName;

    private String body;

    private PublishStatus publishStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}