package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

// for coupon and promotion sale targeted items
public enum TargetType {
    ALL("all"),
    SPECIFIC_CATEGORY("specific_category"),
    SPECIFIC_BRAND("specific_brand"),
    SPECIFIC_ITEM("specific_item");

    private final String value;

    TargetType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
