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

    /**
     * Helper method to get the enum constant from a string value.
     *
     * @param status the string representation of the enum.
     * @return the corresponding RecommendationStatus enum.
     * @throws IllegalArgumentException if the value does not correspond to any enum constant.
     */
    public static RecommendationStatus fromString(String status) {
        for (RecommendationStatus recommendationStatus : RecommendationStatus.values()) {
            if (recommendationStatus.getValue().equalsIgnoreCase(status)) {
                return recommendationStatus;
            }
        }
        throw new IllegalArgumentException("No enum constant for value: " + status);
    }
}
