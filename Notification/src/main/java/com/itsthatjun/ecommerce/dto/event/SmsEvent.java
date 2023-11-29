package com.itsthatjun.ecommerce.dto.event;

import com.itsthatjun.ecommerce.dto.OnSale;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class SmsEvent {

    public enum Type {
        NEW_SALE
    }

    private final Type eventType;
    private final OnSale sale;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public SmsEvent() {
        this.eventType = null;
        this.sale = null;
        this.eventCreatedAt = null;
    }

    public SmsEvent(Type eventType, OnSale returnDetail) {
        this.eventType = eventType;
        this.sale = returnDetail;
        this.eventCreatedAt = now();
    }
}
