package com.itsthatjun.ecommerce.dto.OMS.event;

import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.Map;

import static java.time.ZonedDateTime.now;

@Getter
public class SmsSalesStockEvent {

    public enum Type {
        UPDATE_PURCHASE,   // Generated order, increase sku lock stock
        UPDATE_PURCHASE_PAYMENT, // Generated order and success payment, decrease product stock, decrease sku stock and sku lock stock
        UPDATE_RETURN,  // Generated order and success payment and return, increase product stock and sku stock
        UPDATE_FAIL_PAYMENT  // Generated order and failure payment, decrease sku lock stock
    }

    private final Type eventType;
    private final String orderSN;             // order serial number associated with this used coupon
    private final int userId;
    private final int orderId;
    private final Map<String, Integer> productMap;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public SmsSalesStockEvent() {
        this.eventType = null;
        this.orderSN = null;
        this.userId = 0;
        this.orderId = 0;
        this.productMap = null;
        this.eventCreatedAt = null;
    }

    // update sales stock
    public SmsSalesStockEvent(Type eventType, String orderSN, int orderId, int userId, Map<String, Integer> productMap) {
        this.eventType = eventType;
        this.orderSN = orderSN;
        this.userId = userId ;
        this.orderId = orderId;
        this.productMap = productMap;
        this.eventCreatedAt = now();
    }
}
