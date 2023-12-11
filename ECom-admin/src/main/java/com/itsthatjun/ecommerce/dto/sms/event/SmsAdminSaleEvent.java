package com.itsthatjun.ecommerce.dto.sms.event;

import com.itsthatjun.ecommerce.dto.sms.OnSaleRequest;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class SmsAdminSaleEvent {

    public enum Type {
        CREATE_SALE_LIST,
        CREATE_SALE_BRAND,
        CREATE_SALE_CATEGORY,
        UPDATE_SALE_INFO,
        UPDATE_SALE_PRICE,
        UPDATE_SALE_STATUS,
        DELETE_SALE,
    }

    private final Type eventType;
    private final OnSaleRequest saleRequest;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    @Autowired
    public SmsAdminSaleEvent(Type eventType, OnSaleRequest saleRequest, String operator) {
        this.eventType = eventType;
        this.saleRequest = saleRequest;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
