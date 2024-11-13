package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EmailServiceType {
    USER_SERVICE("user_service"),
    USER_SERVICE_ALL("user_service_all"),
    SALE_SERVICE("sale_service"),
    ORDER_SERVICE("order_service"),
    ORDER_SERVICE_UPDATE("order_service_update"),
    ORDER_SERVICE_RETURN("order_service_return"),
    ORDER_SERVICE_RETURN_UPDATE("order_service_return_update");

    private final String value;

    EmailServiceType(String value) {
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
     * @return the corresponding EmailServiceType enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static EmailServiceType fromString(String status) {
        for (EmailServiceType emailServiceType : EmailServiceType.values()) {
            if (emailServiceType.getValue().equalsIgnoreCase(status)) {
                return emailServiceType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
