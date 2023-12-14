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

    public SmsEvent(Type eventType, OnSale returnDetail) {
        this.eventType = eventType;
        this.sale = returnDetail;
        this.eventCreatedAt = now();
    }
}
