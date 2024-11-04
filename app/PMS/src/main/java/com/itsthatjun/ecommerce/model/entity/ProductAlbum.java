package com.itsthatjun.ecommerce.model.entity;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("product_album")
public class ProductAlbum {
    @Id
    private Integer id;

    private String name;

    private Integer productId;

    private String coverPic;

    private Integer picCount;

    private String description;

    private LocalDateTime createdAt;
}