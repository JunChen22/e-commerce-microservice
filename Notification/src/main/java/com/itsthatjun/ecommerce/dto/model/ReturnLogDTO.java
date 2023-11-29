package com.itsthatjun.ecommerce.dto.model;

import lombok.Data;

import java.util.Date;

@Data
public class ReturnLogDTO {

    private String updateAction;

    private String operator;

    private Date createdAt;
}
