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
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    public SmsAdminCouponEvent(Type eventType, CouponSale couponSale, String operator) {
        this.eventType = eventType;
        this.couponSale = couponSale;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
