package com.itsthatjun.ecommerce.enums.status;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ReturnStatus {
    WAITING_TO_BE_PROCESSED("waiting_to_be_processed"),
    RETURNING("returning"),
    COMPLETE("complete"),
    REJECTED("rejected");  //  not matching return reason

    private final String value;

    ReturnStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}