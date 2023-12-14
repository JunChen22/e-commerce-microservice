package com.itsthatjun.ecommerce.dto.oms.event;

import com.itsthatjun.ecommerce.dto.oms.ReturnRequestDecision;
import lombok.Getter;
import java.time.ZonedDateTime;
import static java.time.ZonedDateTime.now;

@Getter
public class OmsAdminOrderReturnEvent {

    public enum Type {
        WAITING_TO_PROCESS,
        APPROVED, // RETURNING_ITEM_TRANSIT
        REJECTED,
        COMPLETED_RETURN
    }

    private final Type eventType;
    private final ReturnRequestDecision returnRequestDecision;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    public OmsAdminOrderReturnEvent(Type eventType, ReturnRequestDecision returnRequestDecision, String operator) {
        this.eventType = eventType;
        this.operator = operator;
        this.returnRequestDecision = returnRequestDecision;
        this.eventCreatedAt = now();
    }
}
