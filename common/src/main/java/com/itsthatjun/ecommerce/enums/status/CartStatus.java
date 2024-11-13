package com.itsthatjun.ecommerce.enums.status;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CartStatus {
    ACTIVE("active"),
    ABANDONED("abandoned"),
    COMPLETED("completed"),
    EXPIRED("expired");

    private final String value;

    CartStatus(String value) {
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
     * @return the corresponding CartStatus enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static CartStatus fromString(String status) {
        for (CartStatus cartStatus : CartStatus.values()) {
            if (cartStatus.getValue().equalsIgnoreCase(status)) {
                return cartStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
