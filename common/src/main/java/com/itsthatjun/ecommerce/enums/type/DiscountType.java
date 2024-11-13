package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DiscountType {
    PERCENTAGE("percentage"),
    AMOUNT("amount");

    private final String value;

    DiscountType(String value) {
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
     * @return the corresponding DiscountType enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static DiscountType fromString(String status) {
        for (DiscountType discountType : DiscountType.values()) {
            if (discountType.getValue().equalsIgnoreCase(status)) {
                return discountType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
