package com.itsthatjun.ecommerce.dto.event;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

public class SmsCouponEvent {

    public enum Type {
        NEW_SALE
    }

    private final Type eventType;
    private final ZonedDateTime eventCreatedAt;

    public SmsCouponEvent(Type eventType) {
        this.eventType = eventType;
        this.eventCreatedAt = now();
    }
}
