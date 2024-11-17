package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UpdateActionType {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    CANCEL("cancel"),
    CLOSED("closed"),
    OTHER("other");

    private final String value;

    UpdateActionType(String value) {
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
     * @return the corresponding UpdateActionType enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static UpdateActionType fromString(String status) {
        for (UpdateActionType updateActionType : UpdateActionType.values()) {
            if (updateActionType.getValue().equalsIgnoreCase(status)) {
                return updateActionType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
