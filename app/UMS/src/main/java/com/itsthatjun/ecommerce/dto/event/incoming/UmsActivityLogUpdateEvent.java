package com.itsthatjun.ecommerce.dto.event.incoming;

import com.itsthatjun.ecommerce.model.entity.MemberActivityLog;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

import static java.time.ZonedDateTime.now;

@Getter
public class UmsLogUpdateEvent {

    public enum Type {
        New_LOGIN,
        LOG_OFF
    }

    private final Type eventType;
    private final UUID memberId;
    private final MemberActivityLog activityLog;
    private final ZonedDateTime eventCreatedAt;

    public UmsLogUpdateEvent(Type eventType, UUID memberId, MemberActivityLog activityLog) {
        this.eventType = eventType;
        this.memberId = memberId;
        this.activityLog = activityLog;
        this.eventCreatedAt = now();
    }
}
