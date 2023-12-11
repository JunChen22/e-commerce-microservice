package com.itsthatjun.ecommerce.dto.ums.event;

import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class UmsAdminEmailEvent {

    public enum Type {
        ONE_USER,
        ALL_USER
    }

    private final Type eventType;
    private final Integer userId;
    private final String message;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    public UmsAdminEmailEvent(Type eventType, Integer userId, String message, String operator) {
        this.eventType = eventType;
        this.userId = userId;
        this.message = message;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
