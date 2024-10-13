package com.itsthatjun.ecommerce.dto.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class ReturnLogDTO {

    private String updateAction;

    private String operator;

    private Date createdAt;
}
