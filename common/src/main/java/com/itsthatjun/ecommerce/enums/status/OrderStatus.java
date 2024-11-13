package com.itsthatjun.ecommerce.enums.status;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    WAITING_FOR_PAYMENT("waiting_for_payment"),
    FULFILLING("fulfilling"),
    SENT("sent"),
    COMPLETE("complete"),  // received
    CLOSED("closed"), // out of return period
    INVALID("invalid");

    private final String value;

    OrderStatus(String value) {
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
     * @return the corresponding OrderStatus enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static OrderStatus fromString(String status) {
        for (OrderStatus orderStatus : OrderStatus.values()) {
            if (orderStatus.getValue().equalsIgnoreCase(status)) {
                return orderStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}