package com.itsthatjun.ecommerce.dto.event;

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
    private final ZonedDateTime eventCreatedAt;

    public UmsEmailEvent(Type eventType, UserInfo userInfo, String message) {
        this.eventType = eventType;
        this.userInfo = userInfo;
        this.message = message;
        this.eventCreatedAt = now();
    }
}
