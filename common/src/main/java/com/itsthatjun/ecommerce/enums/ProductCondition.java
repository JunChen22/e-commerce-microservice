package com.itsthatjun.ecommerce.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductCondition {
    NEW_PRODUCT("new"),
    USED_PRODUCT("used"),
    REFURBISHED_PRODUCT("refurbished");

    private final String value;

    ProductCondition(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
