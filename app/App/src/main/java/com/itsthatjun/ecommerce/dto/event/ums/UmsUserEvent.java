package com.itsthatjun.ecommerce.dto.event.ums;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class UmsUserEvent {

    public enum Type {
        NEW_ACCOUNT,
        UPDATE_PASSWORD,
        UPDATE_ACCOUNT_INFO,
        UPDATE_ADDRESS,
        DELETE_ACCOUNT
    }

    private final Type eventType;
    private final Integer userId;
    private final MemberDetail memberDetail;
    private final String jwtToken;
    private final ZonedDateTime eventCreatedAt;

    public UmsUserEvent(Type eventType, Integer userId, MemberDetail memberDetail, String jwtToken) {
        this.eventType = eventType;
        this.userId = userId;
        this.memberDetail = memberDetail;
        this.jwtToken = jwtToken;
        this.eventCreatedAt = now();
    }
}
