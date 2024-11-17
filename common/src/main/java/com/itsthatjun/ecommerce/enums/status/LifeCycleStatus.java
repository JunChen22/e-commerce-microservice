package com.itsthatjun.ecommerce.enums.status;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LifeCycleStatus {
    NORMAL("normal"), // Fully active. User can log in and perform all actions.
    SOFT_DELETE("soft_delete"), // User account is disabled, but data remains in place.
    ARCHIVED("archived"), // Similar to soft_deleted but meant for long-term retention. May only be accessible to admins for audits.
    BANNED("banned"); // User is locked out permanently due to policy violation.

    private final String value;

    LifeCycleStatus(String value) {
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
     * @return the corresponding LifeCycleStatus enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static LifeCycleStatus fromString(String status) {
        for (LifeCycleStatus lifeCycleStatus : LifeCycleStatus.values()) {
            if (lifeCycleStatus.getValue().equalsIgnoreCase(status)) {
                return lifeCycleStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
