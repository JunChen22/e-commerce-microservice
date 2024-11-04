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
}
