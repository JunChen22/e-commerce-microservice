package com.itsthatjun.ecommerce.dto.event.incoming;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;

@Getter
public class SmsUpdateIncomingEvent {

    public enum Type {
        UPDATE_SALE_PRICE,
        REMOVE_SALE
    }

    private final Type eventType;
    private final List<ProductSku> skuList;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public SmsUpdateIncomingEvent() {
        this.eventType = null;
        this.skuList = null;
        this.eventCreatedAt = null;
    }

    public SmsUpdateIncomingEvent(Type eventType, List<ProductSku> skuList) {
        this.eventType = eventType;
        this.skuList = skuList;
        this.eventCreatedAt = now();
    }
}
