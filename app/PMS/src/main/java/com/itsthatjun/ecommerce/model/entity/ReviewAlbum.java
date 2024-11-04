package com.itsthatjun.ecommerce.model.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("review_album")
public class ReviewAlbum {
    @Id
    private Integer id;

    private Integer reviewId;

    private Integer picCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}