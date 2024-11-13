package com.itsthatjun.ecommerce.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ProductCondition {
    NEW_PRODUCT("new"),
    USED_PRODUCT("used"),
    REFURBISHED_PRODUCT("refurbished");

    private final String value;

    ProductCondition(String value) {
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
     * @return the corresponding ProductCondition enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static ProductCondition fromString(String status) {
        for (ProductCondition productCondition : ProductCondition.values()) {
            if (productCondition.getValue().equalsIgnoreCase(status)) {
                return productCondition;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
