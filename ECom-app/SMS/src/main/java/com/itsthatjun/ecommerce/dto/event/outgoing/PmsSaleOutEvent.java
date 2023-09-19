package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;

@Getter
public class PmsSaleOutEvent {

    public enum Type {
        CREATE_SALE,
        UPDATE_SALE_PRICE,
        DELETE_SALE,
    }

    private final Type eventType;
    private final List<ProductSku> skuList;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public PmsSaleOutEvent() {
        this.eventType = null;
        this.skuList = null;
        this.eventCreatedAt = null;
    }

    public PmsSaleOutEvent(Type eventType, List<ProductSku> skuList) {
        this.eventType = eventType;
        this.skuList = skuList;
        this.eventCreatedAt = now();
    }
}
