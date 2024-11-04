package com.itsthatjun.ecommerce.enums.status;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PublishStatus {
    PUBLISHED("published"),
    PENDING("pending"),
    DRAFT("draft"),
    PAUSED("paused"),
    DELETED("deleted");

    private final String value;

    PublishStatus(String value) {
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
     * @return the corresponding PublishStatus enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static PublishStatus fromString(String status) {
        for (PublishStatus publishStatus : PublishStatus.values()) {
            if (publishStatus.getValue().equalsIgnoreCase(status)) {
                return publishStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
