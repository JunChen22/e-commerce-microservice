package com.itsthatjun.ecommerce.dto.Event;

import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Map;

import static java.time.ZonedDateTime.now;

@Getter
public class SmsEvent {

    public enum Type {
        UPDATE_COUPON_USAGE,
        UPDATE_SALES_LOCK_STOCK,
        UPDATE_SALES_STOCK,             // purchased and paid
        RETURN_SALES_STOCK              // unpaid or fail payment, stock increase
    }

    private final Type eventType;
    private final String orderSN;             // order serial number associated with this used coupon
    private final String coupon;
    private final int memberId;
    private final int orderId;
    private final Map<String, Integer> productMap;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public SmsEvent() {
        this.eventType = null;
        this.orderSN = null;
        this.coupon = null;
        this.memberId = 0;
        this.orderId = 0;
        this.productMap = null;
        this.eventCreatedAt = null;
    }

    // update coupon usage
    public SmsEvent(Type eventType, String orderSN, String coupon, int memberId, int orderId) {
        this.eventType = eventType;
        this.orderSN = orderSN;
        this.coupon = coupon;
        this.memberId = memberId;
        this.orderId = orderId;
        this.productMap = null;
        this.eventCreatedAt = now();
    }

    // update sales stock
    public SmsEvent(Type eventType, String orderSN, int orderId, Map<String, Integer> productMap) {
        this.eventType = eventType;
        this.coupon = null;
        this.orderSN = orderSN;
        this.memberId = -1;
        this.orderId = orderId;
        this.productMap = productMap;
        this.eventCreatedAt = now();
    }
}
