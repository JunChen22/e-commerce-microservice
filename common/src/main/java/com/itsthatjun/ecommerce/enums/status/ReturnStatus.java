package com.itsthatjun.ecommerce.enums.status;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ReturnStatus {
    WAITING_TO_BE_PROCESSED("waiting_to_be_processed"),
    RETURNING("returning"),
    COMPLETE("complete"),
    REJECTED("rejected");  //  not matching return reason

    private final String value;

    ReturnStatus(String value) {
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
     * @return the corresponding ReturnStatus enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static ReturnStatus fromString(String status) {
        for (ReturnStatus returnStatus : ReturnStatus.values()) {
            if (returnStatus.getValue().equalsIgnoreCase(status)) {
                return returnStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}