package com.itsthatjun.ecommerce.model.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("review_album_picture")
public class ReviewAlbumPicture {
    @Id
    private Integer id;

    private Integer reviewAlbumId;

    private String filename;

    private LocalDateTime createdAt;
}