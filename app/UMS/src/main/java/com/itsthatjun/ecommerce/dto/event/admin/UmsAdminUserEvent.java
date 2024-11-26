package com.itsthatjun.ecommerce.dto.event.admin;

import com.itsthatjun.ecommerce.model.entity.Member;
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
    private final Member member;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    public UmsAdminUserEvent(Type eventType, Member member, String operator) {
        this.eventType = eventType;
        this.member = member;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
