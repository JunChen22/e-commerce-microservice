package com.itsthatjun.ecommerce.dto.outgoing;

import lombok.Data;

import java.util.Date;

@Data
public class ReturnLogDTO {

    private String updateAction;

    private String operator;

    private Date createdAt;
}
