package com.itsthatjun.ecommerce.enums.status;

import com.fasterxml.jackson.annotation.JsonValue;

public enum VerificationStatus {
    VERIFIED("verified"),
    NOT_VERIFIED("not_verified");

    private final String value;

    VerificationStatus(String value) {
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
     * @return the corresponding VerificationStatus enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static VerificationStatus fromString(String status) {
        for (VerificationStatus verificationStatus : VerificationStatus.values()) {
            if (verificationStatus.getValue().equalsIgnoreCase(status)) {
                return verificationStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
