package com.itsthatjun.ecommerce.dto.cms.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ArticleDTO {

    private String title;

    private String slug;

    private String body;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
