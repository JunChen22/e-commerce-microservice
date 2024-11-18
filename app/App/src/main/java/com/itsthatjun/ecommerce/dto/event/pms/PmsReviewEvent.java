package com.itsthatjun.ecommerce.dto.event.pms;

import com.itsthatjun.ecommerce.dto.pms.ProductReview;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

import static java.time.ZonedDateTime.now;

@Getter
public class PmsReviewEvent {

    public enum Type {
        CREATE_REVIEW,
        UPDATE_REVIEW,
        DELETE_REVIEW
    }

    private final Type eventType;
    private final UUID memberId;
    private final ProductReview review;
    private final ZonedDateTime eventCreatedAt;

    public PmsReviewEvent(Type eventType, UUID memberId, ProductReview review) {
        this.eventType = eventType;
        this.memberId = memberId;
        this.review = review;
        this.eventCreatedAt = now();
    }
}
