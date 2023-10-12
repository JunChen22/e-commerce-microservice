package com.itsthatjun.ecommerce.dto.event.admin;

import com.itsthatjun.ecommerce.mbg.model.Member;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class UmsAdminUserEvent {

    public enum Type {
        NEW_ACCOUNT,
        UPDATE_ACCOUNT_STATUS,
        UPDATE_ACCOUNT_INFO,
        DELETE_ACCOUNT
    }

    private final Type eventType;
    private final Integer userId;
    private final Member member;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public UmsAdminUserEvent() {
        this.eventType = null;
        this.userId = 0;
        this.member = null;
        this.operator = null;
        this.eventCreatedAt = null;
    }

    public UmsAdminUserEvent(Type eventType, Integer userId, Member member, String operator) {
        this.eventType = eventType;
        this.userId = userId;
        this.member = member;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
