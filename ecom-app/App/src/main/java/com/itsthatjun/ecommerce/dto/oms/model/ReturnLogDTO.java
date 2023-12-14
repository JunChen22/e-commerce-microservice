package com.itsthatjun.ecommerce.dto.oms.model;

import lombok.Data;

import java.util.Date;

@Data
public class ReturnLogDTO {

    private String updateAction;

    private String operator;

    private Date createdAt;
}
