package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.dto.UserInfo;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class UmsEmailEvent {

    public enum Type {
        ONE_USER,
        ALL_USER
    }

    private final Type eventType;
    private final UserInfo userInfo;
    private final String message;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    public UmsEmailEvent(Type eventType, UserInfo userInfo, String message, String operator) {
        this.eventType = eventType;
        this.userInfo = userInfo;
        this.message = message;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
