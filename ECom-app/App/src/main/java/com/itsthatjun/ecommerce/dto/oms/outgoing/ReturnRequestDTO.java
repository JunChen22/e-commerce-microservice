package com.itsthatjun.ecommerce.dto.oms.outgoing;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class ReturnRequestDTO {

    private String orderSn;

    private Integer returnQuantity;

    private String returnName;

    private String returnPhone;

    private Integer status;

    private Date handleTime;

    private BigDecimal askingAmount;

    private BigDecimal refundedAmount;

    private String reason;

    private String description;

    private String handleNote;

    private String handleOperator;

    private String receiveOperator;

    private Date receiveTime;

    private String receiveNote;

    private Date createdAt;

    private Date updatedAt;
}
