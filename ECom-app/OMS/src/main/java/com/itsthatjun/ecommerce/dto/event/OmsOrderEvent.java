package com.itsthatjun.ecommerce.dto.event;

import com.itsthatjun.ecommerce.dto.OrderParam;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsOrderEvent {

    public enum Type {
        GENERATE_ORDER,
        CANCEL_ORDER,
        UPDATE_ORDER
    }

    private final Type eventType;
    private final int userId;
    private final OrderParam orderParam;  // dto from user to endpoint
    private final String successUrl;
    private final String cancelUrl;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsOrderEvent() {
        this.eventType = null;
        this.userId = 0;
        this.orderParam = null;
        this.successUrl = null;
        this.cancelUrl = null;
        this.eventCreatedAt = null;
    }

    public OmsOrderEvent(Type eventType, int userId, OrderParam orderParam, String successUrl, String cancelUrl) {
        this.eventType = eventType;
        this.userId = userId;
        this.orderParam = orderParam;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.eventCreatedAt = now();
    }
}
