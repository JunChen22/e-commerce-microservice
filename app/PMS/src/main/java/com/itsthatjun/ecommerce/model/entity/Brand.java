package com.itsthatjun.ecommerce.model.entity;

import com.itsthatjun.ecommerce.enums.status.PublishStatus;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("brand")
public class Brand {
    @Id
    private Integer id;

    private String name;

    private String slug;

    private String logo;

    private PublishStatus publishStatus;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}