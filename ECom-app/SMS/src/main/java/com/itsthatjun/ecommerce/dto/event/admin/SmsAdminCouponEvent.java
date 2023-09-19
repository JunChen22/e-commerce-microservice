package com.itsthatjun.ecommerce.dto.event.admin;

import com.itsthatjun.ecommerce.dto.CouponSale;
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
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public SmsAdminCouponEvent() {
        this.eventType = null;
        this.couponSale = null;
        this.eventCreatedAt = null;
    }

    public SmsAdminCouponEvent(Type eventType, CouponSale couponSale) {
        this.eventType = eventType;
        this.couponSale = couponSale;
        this.eventCreatedAt = now();
    }
}
