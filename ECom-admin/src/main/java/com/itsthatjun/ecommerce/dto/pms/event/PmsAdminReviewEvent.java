package com.itsthatjun.ecommerce.dto.pms.event;

import com.itsthatjun.ecommerce.dto.pms.ProductReview;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class PmsAdminReviewEvent {

    public enum Type {
        CREATE,
        UPDATE,
        DELETE
    }

    private final Type eventType;
    private final ProductReview review;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public PmsAdminReviewEvent() {
        this.eventType = null;
        this.review = null;
        this.operator = null;
        this.eventCreatedAt = null;
    }

    public PmsAdminReviewEvent(Type eventType, ProductReview review, String operator) {
        this.eventType = eventType;
        this.review = review;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
