package com.itsthatjun.ecommerce.enums.status;

import com.fasterxml.jackson.annotation.JsonValue;

public enum RecommendationStatus {
    FRONT_PAGE("frontpage"),
    TRENDING("trending"),
    POPULAR("popular"),
    RECOMMENDED("recommended"),
    NEW_ARRIVAL("new_arrival"),
    SEASONAL("seasonal"),
    FLASH_SALE("flash_sale"),
    NORMAL("normal"),
    HIDDEN("hidden");

    private final String value;

    RecommendationStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
