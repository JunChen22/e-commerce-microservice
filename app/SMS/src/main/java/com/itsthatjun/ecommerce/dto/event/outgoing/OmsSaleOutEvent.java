package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;

@Getter
public class OmsSaleOutEvent {

    public enum Type {
        UPDATE_SALE_PRICE,
        DELETE_SALE,
    }

    private final Type eventType;
    private final List<ProductSku> skuList;
    private final ZonedDateTime eventCreatedAt;

    public OmsSaleOutEvent(Type eventType, List<ProductSku> skuList) {
        this.eventType = eventType;
        this.skuList = skuList;
        this.eventCreatedAt = now();
    }
}
