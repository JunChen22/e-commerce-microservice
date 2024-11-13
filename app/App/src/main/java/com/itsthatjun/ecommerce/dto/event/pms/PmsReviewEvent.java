package com.itsthatjun.ecommerce.dto.event.pms;

import com.itsthatjun.ecommerce.dto.pms.ProductReview;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class PmsReviewEvent {

    public enum Type {
        CREATE_REVIEW,
        UPDATE_REVIEW,
        DELETE_REVIEW
    }

    private final Type eventType;
    private final int userId;
    private final ProductReview review;
    private final ZonedDateTime eventCreatedAt;

    public PmsReviewEvent(Type eventType, int userId, ProductReview review) {
        this.eventType = eventType;
        this.userId = userId;
        this.review = review;
        this.eventCreatedAt = now();
    }
}
