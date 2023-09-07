package com.itsthatjun.ecommerce.dto.Event;

import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.time.ZonedDateTime.now;

@Getter
public class PmsEvent {
    public enum Type {
        UPDATE_PURCHASE,  // update stock after order generated, locked stock increase
        UPDATE_PURCHASE_PAYMENT,  // update after payment,  unlock locked stock and update stock.
        UPDATE_RETURN    // unlock returned or unpaid orders stock
    }

    private final Type eventType;
    private final String orderSN;                         // order serial number associated with this stock update
    private final Map<String, Integer> productMap;   // sku and quantity
    private final ZonedDateTime eventCreatedAt;

    public PmsEvent() {
        this.eventType = null;
        this.orderSN = null;
        this.productMap = null;
        this.eventCreatedAt = null;
    }

    public PmsEvent(Type eventType, String orderSN, Map<String, Integer> productMap) {
        this.eventType = eventType;
        this.orderSN = orderSN;
        this.productMap = productMap;
        this.eventCreatedAt = now();
    }
}
