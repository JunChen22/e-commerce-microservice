package com.itsthatjun.ecommerce.dto.event.oms;

import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsCompletionEvent {

    public enum Type {
        PAYMENT_SUCCESS,
        PAYMENT_FAILURE
    }

    // TODO:might make a smaller DTO
    private final Type eventType;
    private final String orderSN;
    private final String paymentId;
    private final String payerId;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsCompletionEvent() {
        this.eventType = null;
        this.orderSN = null;
        this.paymentId = null;
        this.payerId = null;
        this.eventCreatedAt = null;
    }

    // success payment
    public OmsCompletionEvent(Type eventType, String orderSN, String paymentId, String payerId) {
        this.eventType = eventType;
        this.orderSN = orderSN;
        this.paymentId = paymentId;
        this.payerId = payerId;
        this.eventCreatedAt = now();
    }

    // fail payment
    public OmsCompletionEvent(Type eventType, String orderSN) {
        this.eventType = eventType;
        this.orderSN = orderSN;
        this.paymentId = null;
        this.payerId = null;
        this.eventCreatedAt = now();
    }
}
