package com.itsthatjun.ecommerce.model.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("product_album_picture")
public class ProductAlbumPicture {
    @Id
    private Integer id;

    private Integer productAlbumId;

    private String filename;

    private LocalDateTime createdAt;
}