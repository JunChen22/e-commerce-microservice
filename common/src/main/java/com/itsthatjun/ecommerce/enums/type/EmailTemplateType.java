package com.itsthatjun.ecommerce.enums.type;

import com.fasterxml.jackson.annotation.JsonValue;

public enum EmailTemplateType {

    // ADMIN
    ADMIN_MESSAGE_ONE("admin_message_one"),
    ADMIN_MESSAGE_ALL("admin_message_all"),

    // CMS
    CONTENT_SERVICE_ARTICLE("content_service_article"),
    CONTENT_SERVICE_NEWSLETTER("content_service_newsletter"),

    // OMS
    ORDER_SERVICE_CONFIRMATION("order_service_confirmation"),
    ORDER_SERVICE_UPDATE("order_service_update"),
    ORDER_SERVICE_RETURN("order_service_return"),
    ORDER_SERVICE_RETURN_UPDATE("order_service_return_update"),
    ORDER_SERVICE_CANCELLATION("order_service_cancellation"),

    // PMS
    PRODUCT_SERVICE_NEW("product_service_new"),
    PRODUCT_SERVICE_UPDATE("product_service_update"),

    // SMS
    SALE_SERVICE_NEW("sale_service_new"),
    SALE_SERVICE_UPDATE("sale_service_update"),
    SALE_SERVICE_DISCOUNT_REMINDER("sale_service_discount_reminder"),

    // UMS
    USER_SERVICE_ONE("user_service_one"),
    USER_SERVICE_ALL("user_service_all"),
    USER_SERVICE_WELCOME("user_service_welcome"),

    // AUTH
    AUTH_SERVICE_LOGIN_ERROR("auth_service_login_error"),
    AUTH_SERVICE_PASSWORD_RESET("auth_service_password_reset");

    private final String value;

    EmailTemplateType(String value) {
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
     * @return the corresponding EmailServiceType enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static EmailTemplateType fromString(String status) {
        for (EmailTemplateType emailTemplateType : EmailTemplateType.values()) {
            if (emailTemplateType.getValue().equalsIgnoreCase(status)) {
                return emailTemplateType;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
