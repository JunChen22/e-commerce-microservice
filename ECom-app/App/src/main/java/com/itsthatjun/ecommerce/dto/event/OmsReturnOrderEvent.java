package com.itsthatjun.ecommerce.dto.event;

import com.itsthatjun.ecommerce.mbg.model.ReturnRequest;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsReturnOrderEvent {

    public enum Type {
        APPLY,
        UPDATE,
        CANCEL
    }

    private final Type eventType;
    private final int userId;
    private final ReturnRequest returnRequest;
    private final String returnReason;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsReturnOrderEvent() {
        this.eventType = null;
        this.userId = 0;
        this.returnRequest = null;
        this.returnReason = null;
        this.eventCreatedAt = null;
    }

    public OmsReturnOrderEvent(Type eventType, int userId, ReturnRequest returnRequest, String returnReason) {
        this.eventType = eventType;
        this.userId = userId;
        this.returnRequest = returnRequest;
        this.returnReason = returnReason;
        this.eventCreatedAt = now();
    }
}
