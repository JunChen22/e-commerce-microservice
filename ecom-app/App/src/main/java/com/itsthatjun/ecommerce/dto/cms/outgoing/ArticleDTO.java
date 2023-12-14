package com.itsthatjun.ecommerce.dto.cms.outgoing;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleDTO {

    private String title;

    private String body;

    private Date createdAt;

    private Date updatedAt;
}
