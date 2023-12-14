package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.dto.ReturnDetail;
import com.itsthatjun.ecommerce.dto.UserInfo;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsReturnEvent {

    public enum Type {
        NEW_RETURN,
        RETURN_UPDATE
    }

    private final Type eventType;
    private final ReturnDetail returnDetail;
    private final UserInfo userInfo;
    private final ZonedDateTime eventCreatedAt;

    public OmsReturnEvent(Type eventType, ReturnDetail returnDetail, UserInfo userInfo) {
        this.eventType = eventType;
        this.returnDetail = returnDetail;
        this.userInfo = userInfo;
        this.eventCreatedAt = now();
    }
}
