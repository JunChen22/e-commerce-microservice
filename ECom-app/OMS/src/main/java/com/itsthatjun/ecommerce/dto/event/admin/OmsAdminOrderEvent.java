package com.itsthatjun.ecommerce.dto.event.admin;

import com.itsthatjun.ecommerce.dto.admin.AdminOrderDetail;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsAdminOrderEvent {

    public enum Type {
        GENERATE_ORDER,
        CANCEL_ORDER,
        UPDATE_ORDER
    }

    private final Type eventType;
    private final AdminOrderDetail orderDetail;
    private final String reason;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsAdminOrderEvent() {
        this.eventType = null;
        this.orderDetail = null;
        this.reason = null;
        this.operator = null;
        this.eventCreatedAt = null;
    }

    public OmsAdminOrderEvent(Type eventType, AdminOrderDetail OrderDetail, String reason, String operator) {
        this.eventType = eventType;
        this.orderDetail = OrderDetail;
        this.reason = reason;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
