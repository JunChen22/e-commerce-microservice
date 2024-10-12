package com.itsthatjun.ecommerce.dto.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class ArticleDTO {

    private String title;

    private String slug;

    private String body;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
