package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EmailTemplateType {
    USER_SERVICE("USER_SERVICE"),
    USER_SERVICE_ALL("USER_SERVICE_ALL"),
    SALE_SERVICE("SALE_SERVICE"),
    ORDER_SERVICE("ORDER_SERVICE"),
    ORDER_SERVICE_UPDATE("ORDER_SERVICE_UPDATE"),
    ORDER_SERVICE_RETURN("ORDER_SERVICE_RETURN"),
    ORDER_SERVICE_RETURN_UPDATE("ORDER_SERVICE_RETURN_UPDATE");

    private final String value;

    EmailTemplateType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}