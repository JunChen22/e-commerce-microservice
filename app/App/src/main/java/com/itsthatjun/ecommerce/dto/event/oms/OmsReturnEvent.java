package com.itsthatjun.ecommerce.dto.event.oms;

import com.itsthatjun.ecommerce.dto.ReturnParam;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsReturnEvent {

    public enum Type {
        APPLY,
        UPDATE,
        CANCEL,
        REJECT
    }

    private final Type eventType;
    private final int userId;
    private final ReturnParam returnParam;
    private final ZonedDateTime eventCreatedAt;

    public OmsReturnEvent(Type eventType, int userId, ReturnParam returnParam) {
        this.eventType = eventType;
        this.userId = userId;
        this.returnParam = returnParam;
        this.eventCreatedAt = now();
    }
}
