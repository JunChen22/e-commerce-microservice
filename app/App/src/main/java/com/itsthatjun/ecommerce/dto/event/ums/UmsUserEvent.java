package com.itsthatjun.ecommerce.dto.event.ums;

import com.itsthatjun.ecommerce.dto.ums.MemberDetail;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

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
    private final UUID memberId;
    private final MemberDetail memberDetail;
    private final String jwtToken;
    private final ZonedDateTime eventCreatedAt;

    public UmsUserEvent(Type eventType, UUID memberId, MemberDetail memberDetail, String jwtToken) {
        this.eventType = eventType;
        this.memberId = memberId;
        this.memberDetail = memberDetail;
        this.jwtToken = jwtToken;
        this.eventCreatedAt = now();
    }
}
