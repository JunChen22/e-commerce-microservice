package com.itsthatjun.ecommerce.dto.event.admin;

import com.itsthatjun.ecommerce.dto.ReturnRequestDecision;
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
    private final String adminName;
    private final ReturnRequestDecision returnRequestDecision;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsAdminOrderReturnEvent() {
        this.eventType = null;
        this.adminName = null;
        this.returnRequestDecision = null;
        this.eventCreatedAt = null;
    }

    public OmsAdminOrderReturnEvent(Type eventType, String adminName, ReturnRequestDecision returnRequestDecision) {
        this.eventType = eventType;
        this.adminName = adminName;
        this.returnRequestDecision = returnRequestDecision;
        this.eventCreatedAt = now();
    }
}
