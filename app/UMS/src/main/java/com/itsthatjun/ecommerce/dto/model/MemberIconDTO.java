package com.itsthatjun.ecommerce.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class MemberIconDTO {

    private final UUID memberId;

    private final String filename;
}
