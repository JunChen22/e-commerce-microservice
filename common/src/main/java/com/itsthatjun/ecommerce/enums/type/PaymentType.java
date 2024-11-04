package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PaymentType {
    CREDIT_CARD("credit_card"),
    PAYPAL("paypal"),
    GOOGLE_PAY("google_pay");

    private final String value;

    PaymentType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
