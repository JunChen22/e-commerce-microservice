package com.itsthatjun.ecommerce.dto.cms.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleDTO {

    private String title;

    private String slug;

    private String body;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
