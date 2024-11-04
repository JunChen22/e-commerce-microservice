package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SourceType {
    WEB("web"),
    MOBILE("mobile");

    private final String value;

    SourceType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
