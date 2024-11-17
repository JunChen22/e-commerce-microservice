package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UserActivityType {
    LOGIN("login"),            // Successful login
    LOGOFF("logoff"),          // User logs off
    FAIL_LOGIN("failed_login"), // Failed login attempt
    PASSWORD_CHANGE("password_change"), // Password change event
    SESSION_EXPIRED("session_expired"), // Session expired due to timeout/inactivity
    TWO_FA_SUCCESS("2fa_success"), // Successful 2FA verification
    TWO_FA_FAILED("2fa_failed"),  // Failed 2FA attempt
    ACCOUNT_LOCKOUT("account_lockout"); // Account locked due to security reasons

    private final String value;

    UserActivityType(String value) {
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
     * @return the corresponding UserActivityType enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static UserActivityType fromString(String status) {
        for (UserActivityType activityType : UserActivityType.values()) {
            if (activityType.getValue().equalsIgnoreCase(status)) {
                return activityType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}