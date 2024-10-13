package com.itsthatjun.ecommerce.dto.event.incoming;

import com.itsthatjun.ecommerce.model.entity.Review;
import com.itsthatjun.ecommerce.model.entity.ReviewPictures;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

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
    private final Review review;
    private final List<ReviewPictures> picturesList;
    private final ZonedDateTime eventCreatedAt;

    public PmsReviewEvent(Type eventType, int userId, Review review, List<ReviewPictures> picturesList) {
        this.eventType = eventType;
        this.userId = userId;
        this.review = review;
        this.picturesList = picturesList;
        this.eventCreatedAt = now();
    }
}
