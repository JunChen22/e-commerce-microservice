package com.itsthatjun.ecommerce.dto.event;

import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsOrderAnnouncementEvent {

    public enum Type {
        ORDER_ITEM_SKU,      // announcement to users that purchased certain item, E.G invalid order, refund, not enough stock or etc
        ORDER_ITEM_PRODUCT
    }

    private final Type eventType;
    private final String productName;       // product name + sku(if applicable)
    private final String message;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    public OmsOrderAnnouncementEvent(Type eventType, String productName, String message, String operator) {
        this.eventType = eventType;
        this.productName = productName;
        this.message = productName;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
