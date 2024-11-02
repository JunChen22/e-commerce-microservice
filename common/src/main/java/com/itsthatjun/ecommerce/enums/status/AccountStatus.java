package com.itsthatjun.ecommerce.enums.status;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * for user/member and admin status
 */
public enum Status {
    ACTIVE("active"),
    SUSPENDED("suspended"),
    INACTIVE("inactive");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}