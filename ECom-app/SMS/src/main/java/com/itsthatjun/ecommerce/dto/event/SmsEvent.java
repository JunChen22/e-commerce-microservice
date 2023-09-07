package com.itsthatjun.ecommerce.dto.event;

import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class SmsEvent {

    public enum Type {
        UPDATE_COUPON_USAGE
    }

    private final Type eventType;
    private final String orderSN;             // order serial number associated with this used coupon
    private final String coupon;
    private final int memberId;
    private final int orderId;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public SmsEvent() {
        this.eventType = null;
        this.orderSN = null;
        this.coupon = null;
        this.memberId = 0;
        this.orderId = 0;
        this.eventCreatedAt = null;
    }

    public SmsEvent(Type eventType, String orderSN, String coupon, int memberId, int orderId) {
        this.eventType = eventType;
        this.orderSN = orderSN;
        this.coupon = coupon;
        this.memberId = memberId;
        this.orderId = orderId;
        this.eventCreatedAt = now();
    }
}
