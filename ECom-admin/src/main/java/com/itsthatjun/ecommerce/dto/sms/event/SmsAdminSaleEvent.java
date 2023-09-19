package com.itsthatjun.ecommerce.dto.sms.event;

import com.itsthatjun.ecommerce.dto.sms.OnSaleRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class SmsAdminSaleEvent {

    public enum Type {
        CREATE_SALE,
        UPDATE_SALE,
        DELETE_SALE,
    }

    private final Type eventType;
    private final OnSaleRequest saleRequest;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public SmsAdminSaleEvent() {
        this.eventType = null;
        this.saleRequest = null;
        this.eventCreatedAt = null;
    }

    @Autowired
    public SmsAdminSaleEvent(Type eventType, OnSaleRequest saleRequest) {
        this.eventType = eventType;
        this.saleRequest = saleRequest;
        this.eventCreatedAt = now();
    }
}
