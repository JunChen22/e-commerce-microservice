package com.itsthatjun.ecommerce.enums.status;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    WAITING_FOR_PAYMENT("waiting for payment"),
    FULFILLING("fulfilling"),
    SENT("sent"),
    COMPLETE("complete"),  // received
    CLOSED("closed"), // out of return period
    INVALID("invalid");

    private final String value;

    OrderStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}