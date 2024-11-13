package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SalesType {
    NOT_ON_SALE("not_on_sale"),
    IS_ON_SALE("is_on_sale"),
    FLASH_SALE("flash_sale"),
    SPECIAL_SALES("special_sales"),
    CLEARANCE("clearance"),
    USED_ITEM("used_item");

    private final String value;

    SalesType(String value) {
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
     * @return the corresponding SalesType enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static SalesType fromString(String status) {
        for (SalesType salesType : SalesType.values()) {
            if (salesType.getValue().equalsIgnoreCase(status)) {
                return salesType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
