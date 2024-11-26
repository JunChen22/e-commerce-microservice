package com.itsthatjun.ecommerce.model.entity;

import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("member_icon")
public class MemberIcon {
    @Id
    private Integer id;

    private UUID memberId;

    private String filename;
}