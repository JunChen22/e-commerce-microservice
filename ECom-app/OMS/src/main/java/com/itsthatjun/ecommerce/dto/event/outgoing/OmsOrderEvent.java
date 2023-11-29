package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.dto.OrderDetail;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsOrderEvent {

    public enum Type {
        ORDER_NEW,
        ORDER_UPDATE,
        ORDER_CANCEL
    }

    private final Type eventType;
    private final OrderDetail orderDetail;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsOrderEvent() {
        this.eventType = null;
        this.orderDetail = null;
        this.eventCreatedAt = null;
    }

    public OmsOrderEvent(Type eventType, OrderDetail orderDetail) {
        this.eventType = eventType;
        this.orderDetail = orderDetail;
        this.eventCreatedAt = now();
    }
}
