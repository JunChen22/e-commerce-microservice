package com.itsthatjun.ecommerce.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ServiceName {
    ADMIN("admin"),
    OMS("oms"),
    CMS("cms"),
    PMS("pms"),
    SMS("sms"),
    UMS("ums");

    private final String value;

    ServiceName(String value) {
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
     * @return the corresponding ServiceName enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static ServiceName fromString(String status) {
        for (ServiceName serviceName : ServiceName.values()) {
            if (serviceName.getValue().equalsIgnoreCase(status)) {
                return serviceName;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
