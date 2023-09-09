package com.itsthatjun.ecommerce.dto.event;

import com.itsthatjun.ecommerce.mbg.model.OrderReturnApply;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnReason;
import com.itsthatjun.ecommerce.mbg.model.OrderReturnReasonPictures;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsReturnOrderEvent {

    public enum Type {
        APPLY,
        UPDATE,
        CANCEL
    }

    private final Type eventType;
    private final int userId;
    private final String orderSn;
    private final OrderReturnApply returnApply;
    private final OrderReturnReason returnReason;
    private final List<OrderReturnReasonPictures> pictures;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public OmsReturnOrderEvent() {
        this.eventType = null;
        this.userId = 0;
        this.orderSn = null;
        this.returnApply = null;
        this.returnReason = null;
        this.pictures = null;
        this.eventCreatedAt = null;
    }

    public OmsReturnOrderEvent(Type eventType, int userId, String orderSn, OrderReturnApply returnApply, OrderReturnReason returnReason,
                               List<OrderReturnReasonPictures> pictures) {
        this.eventType = eventType;
        this.userId = userId;
        this.orderSn = orderSn;
        this.returnApply = returnApply;
        this.returnReason = returnReason;
        this.pictures = pictures;
        this.eventCreatedAt = now();
    }
}
