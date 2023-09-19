package com.itsthatjun.ecommerce.dto.event.admin;

import com.itsthatjun.ecommerce.dto.ProductReview;
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
    private final Integer reviewId;
    private final ProductReview review;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public PmsAdminReviewEvent() {
        this.eventType = null;
        this.review = null;
        this.reviewId = null;
        this.eventCreatedAt = null;
    }

    public PmsAdminReviewEvent(Type eventType, ProductReview review, Integer reviewId) {
        this.eventType = eventType;
        this.review = review;
        this.reviewId = reviewId;
        this.eventCreatedAt = now();
    }
}
