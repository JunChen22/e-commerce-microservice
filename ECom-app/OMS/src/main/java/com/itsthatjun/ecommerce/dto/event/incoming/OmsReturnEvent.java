package com.itsthatjun.ecommerce.dto.event.incoming;

import com.itsthatjun.ecommerce.dto.ReturnParam;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsReturnEvent {

    public enum Type {
        APPLY,
        UPDATE,
        CANCEL
    }

    private final Type eventType;
    private final int userId;
    private final String orderSn;
    private final ReturnParam returnParam;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsReturnEvent() {
        this.eventType = null;
        this.userId = 0;
        this.orderSn = null;
        this.returnParam = null;
        this.eventCreatedAt = null;
    }

    public OmsReturnEvent(Type eventType, int userId, String orderSn, ReturnParam returnParam) {
        this.eventType = eventType;
        this.userId = userId;
        this.orderSn = orderSn;
        this.returnParam = returnParam;
        this.eventCreatedAt = now();
    }
}
