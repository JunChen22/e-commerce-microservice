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
    private final ProductReview review;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    public PmsAdminReviewEvent(Type eventType, ProductReview review, String operator) {
        this.eventType = eventType;
        this.review = review;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
