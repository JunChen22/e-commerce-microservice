package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PlatformType {
    WEB("web"),
    IOS("ios"),
    ANDROID("android");

    private final String value;

    PlatformType(String value) {
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
     * @return the corresponding SourceType enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static PlatformType fromString(String status) {
        for (PlatformType sourceType : PlatformType.values()) {
            if (sourceType.getValue().equalsIgnoreCase(status)) {
                return sourceType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
