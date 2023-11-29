package com.itsthatjun.ecommerce.dto.event;

import com.itsthatjun.ecommerce.dto.OnSale;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

public class SmsCouponEvent {

    public enum Type {
        NEW_SALE
    }

    private final Type eventType;

    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public SmsCouponEvent() {
        this.eventType = null;
        this.eventCreatedAt = null;
    }

    public SmsCouponEvent(Type eventType) {
        this.eventType = eventType;
        this.eventCreatedAt = now();
    }
}
