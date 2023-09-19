package com.itsthatjun.ecommerce.dto.event.outgoing;

import com.itsthatjun.ecommerce.mbg.model.Product;
import com.itsthatjun.ecommerce.mbg.model.ProductSku;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

import static java.time.ZonedDateTime.now;

@Getter
public class SmsProductOutEvent {

    public enum Type {
        NEW_PRODUCT,
        NEW_PRODUCT_SKU,
        UPDATE_STOCK,
        UPDATE_PRODUCT_PRICE,
        REMOVE_PRODUCT,
        REMOVE_PRODUCT_SKU
    }

    private final OmsProductOutEvent.Type eventType;
    private final Product product;
    private final List<ProductSku> productSkuList;
    private final ZonedDateTime eventCreatedAt;

    public SmsProductOutEvent() {
        this.eventType = null;
        this.product = null;
        this.productSkuList = null;
        this.eventCreatedAt = null;
    }

    public SmsProductOutEvent(OmsProductOutEvent.Type eventType, Product product, List<ProductSku> productSkuList) {
        this.eventType = eventType;
        this.product = product;
        this.productSkuList = productSkuList;
        this.eventCreatedAt = now();
    }
}
