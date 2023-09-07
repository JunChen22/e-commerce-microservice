package com.itsthatjun.ecommerce.dto.Event;

import com.itsthatjun.ecommerce.mbg.model.OrderReturnApply;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnReason;
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
    private final OrderReturnApply returnApply;
    private final OrderReturnReason returnReason;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsReturnOrderEvent() {
        this.eventType = null;
        this.userId = 0;
        this.returnApply = null;
        this.returnReason = null;
        this.eventCreatedAt = null;
    }

    public OmsReturnOrderEvent(Type eventType, int userId, OrderReturnApply returnApply, OrderReturnReason returnReason) {
        this.eventType = eventType;
        this.userId = userId;
        this.returnApply = returnApply;
        this.returnReason = returnReason;
        this.eventCreatedAt = now();
    }
}
