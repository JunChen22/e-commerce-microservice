package com.itsthatjun.ecommerce.model.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Table("article")
public class Article {
    @Id
    private Integer id;

    private String title;

    private String slug;

    private String body;

    private Integer publishStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Generate slug based on title
    public void generateSlug() {
        this.slug = title.toLowerCase().replaceAll("[^a-z0-9-]", "-").replaceAll("--", "-");
        this.slug = this.slug.replaceAll("-+", "-"); // Replace multiple hyphens with one
    }
}