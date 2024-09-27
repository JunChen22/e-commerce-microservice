package com.itsthatjun.ecommerce.dto.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderDTO {

    private String orderSn;

    private String memberEmail;

    private BigDecimal totalAmount;

    private BigDecimal promotionAmount;

    private BigDecimal couponAmount;

    private BigDecimal discountAmount;

    private BigDecimal shippingCost;

    private BigDecimal payAmount;

    private String receiverPhone;

    private String receiverName;

    private String receiverDetailAddress;

    private String receiverCity;

    private String receiverState;

    private String receiverZipCode;

    private String deliveryCompany;

    private String deliveryTrackingNumber;

    private Date deliveryTime;

    private Integer status;

    private String comment;
}
