package com.itsthatjun.ecommerce.dto.event.incoming;

import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Map;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsStockUpdateIncomingEvent {

    public enum Type {
        UPDATE_PURCHASE,   // Generated order, increase sku lock stock
        UPDATE_PURCHASE_PAYMENT, // Generated order and success payment, decrease product stock, decrease sku stock and sku lock stock
        UPDATE_RETURN,  // Generated order and success payment and return, increase product stock and sku stock
        UPDATE_FAIL_PAYMENT  // Generated order and failure payment, decrease sku lock stock
    }

    private final Type eventType;
    private final String orderSn;             // order serial number associated with this sales stock update
    private final Map<String, Integer> productMap;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsStockUpdateIncomingEvent() {
        this.eventType = null;
        this.orderSn = null;
        this.productMap = null;
        this.eventCreatedAt = null;
    }

    // update sales stock
    public OmsStockUpdateIncomingEvent(Type eventType, String orderSn, Map<String, Integer> productMap) {
        this.eventType = eventType;
        this.orderSn = orderSn;
        this.productMap = productMap;
        this.eventCreatedAt = now();
    }
}
