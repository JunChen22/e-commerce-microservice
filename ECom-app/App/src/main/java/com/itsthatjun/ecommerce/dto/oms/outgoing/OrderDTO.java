package com.itsthatjun.ecommerce.dto.oms.outgoing;

import lombok.Data;

import java.util.Date;

@Data
public class OrderDTO {

    private String orderSn;

    private String memberEmail;

    private double totalAmount;

    private double promotionAmount;

    private double couponAmount;

    private double discountAmount;

    private double shippingCost;

    private double payAmount;

    private String receiverPhone;

    private String receiverName;

    private String receiverDetailAddress;

    private String receiverCity;

    private String receiverState;

    private String receiverZipCode;

    private String deliveryCompany;

    private String deliveryTrackingNumber;

    private Date deliveryTime;

    private String comment;
}
