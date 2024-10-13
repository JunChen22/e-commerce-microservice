package com.itsthatjun.ecommerce.model.entity;

import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

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