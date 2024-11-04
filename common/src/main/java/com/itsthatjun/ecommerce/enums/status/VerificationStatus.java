package com.itsthatjun.ecommerce.enums.status;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VerificationStatus {
    VERIFIED("verified"),
    NOT_VERIFIED("not verified");

    private final String value;

    VerificationStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
