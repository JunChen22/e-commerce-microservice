package com.itsthatjun.ecommerce.dto.event.admin;

import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

import static java.time.ZonedDateTime.now;

@Getter
public class UmsAdminEmailEvent {

    public enum Type {
        ONE_USER,
        ALL_USER
    }

    private final Type eventType;
    private final UUID memberId;
    private final String message;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    public UmsAdminEmailEvent(Type eventType, UUID memberId, String message, String operator) {
        this.eventType = eventType;
        this.memberId = memberId;
        this.message = message;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
