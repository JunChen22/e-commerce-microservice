package com.itsthatjun.ecommerce.dto.event.admin;

import com.itsthatjun.ecommerce.model.AdminProductDetail;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class PmsAdminProductEvent {

    public enum Type {
        NEW_PRODUCT,
        NEW_PRODUCT_SKU,
        UPDATE_STOCK,
        UPDATE_PRODUCT_INFO,
        UPDATE_PRODUCT_PRICE,
        UPDATE_PRODUCT_STATUS,
        UPDATE_PRODUCT_SKU_STATUS,
        REMOVE_PRODUCT_SKU,
        DELETE_PRODUCT
    }

    private final Type eventType;
    private final AdminProductDetail productDetail;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    public PmsAdminProductEvent(Type eventType, AdminProductDetail productDetail, String operator) {
        this.eventType = eventType;
        this.productDetail = productDetail;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
