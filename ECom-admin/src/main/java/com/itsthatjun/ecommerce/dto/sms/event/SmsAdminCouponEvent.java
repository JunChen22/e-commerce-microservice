package com.itsthatjun.ecommerce.dto.sms.event;

import com.itsthatjun.ecommerce.dto.sms.CouponSale;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class SmsAdminCouponEvent {

    public enum Type {
        CREATE_COUPON,
        UPDATE_COUPON,
        DELETE_COUPON
    }

    private final Type eventType;
    private final CouponSale couponSale;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public SmsAdminCouponEvent() {
        this.eventType = null;
        this.couponSale = null;
        this.operator = null;
        this.eventCreatedAt = null;
    }

    public SmsAdminCouponEvent(Type eventType, CouponSale couponSale, String operator) {
        this.eventType = eventType;
        this.couponSale = couponSale;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
