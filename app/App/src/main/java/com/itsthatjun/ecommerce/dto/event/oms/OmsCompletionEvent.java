package com.itsthatjun.ecommerce.dto.event.oms;

import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsCompletionEvent {

    public enum Type {
        PAYMENT_SUCCESS,
        PAYMENT_FAILURE // not used right now since it's TTL to cancel.
    }

    private final Type eventType;
    private final String orderSn;
    private final String paymentId;
    private final String payerId;
    private final ZonedDateTime eventCreatedAt;

    // success payment
    public OmsCompletionEvent(Type eventType, String orderSn, String paymentId, String payerId) {
        this.eventType = eventType;
        this.orderSn = orderSn;
        this.paymentId = paymentId;
        this.payerId = payerId;
        this.eventCreatedAt = now();
    }

    // fail payment
    public OmsCompletionEvent(Type eventType, String orderSn) {
        this.eventType = eventType;
        this.orderSn = orderSn;
        this.paymentId = null;
        this.payerId = null;
        this.eventCreatedAt = now();
    }
}
