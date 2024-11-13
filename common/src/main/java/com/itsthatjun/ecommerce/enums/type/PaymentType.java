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

    /**
     * Helper method to get the enum constant from a string value.
     *
     * @param status the string representation of the enum.
     * @return the corresponding PaymentType enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static PaymentType fromString(String status) {
        for (PaymentType paymentType : PaymentType.values()) {
            if (paymentType.getValue().equalsIgnoreCase(status)) {
                return paymentType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
