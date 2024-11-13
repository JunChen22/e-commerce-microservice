package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * for coupon and promotion sale targeted items
 */
public enum TargetType {
    ALL("all"),
    SPECIFIC_CATEGORY("specific_category"),
    SPECIFIC_BRAND("specific_brand"),
    SPECIFIC_ITEM("specific_item");

    private final String value;

    TargetType(String value) {
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
     * @return the corresponding TargetType enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static TargetType fromString(String status) {
        for (TargetType targetType : TargetType.values()) {
            if (targetType.getValue().equalsIgnoreCase(status)) {
                return targetType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
