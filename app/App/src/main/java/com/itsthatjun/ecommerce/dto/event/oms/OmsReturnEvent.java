package com.itsthatjun.ecommerce.dto.event.oms;

import com.itsthatjun.ecommerce.dto.oms.ReturnParam;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

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
    private final UUID memberId;
    private final ReturnParam returnParam;
    private final ZonedDateTime eventCreatedAt;

    public OmsReturnEvent(Type eventType, UUID memberId, ReturnParam returnParam) {
        this.eventType = eventType;
        this.memberId = memberId;
        this.returnParam = returnParam;
        this.eventCreatedAt = now();
    }
}
