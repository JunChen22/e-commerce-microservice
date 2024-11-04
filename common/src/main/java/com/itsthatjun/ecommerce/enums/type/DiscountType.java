package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DiscountType {
    PERCENTAGE("percentage"),
    AMOUNT("amount");

    private final String value;

    DiscountType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
