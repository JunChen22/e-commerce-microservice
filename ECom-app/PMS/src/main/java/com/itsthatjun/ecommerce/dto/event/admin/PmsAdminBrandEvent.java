package com.itsthatjun.ecommerce.dto.event.admin;

import com.itsthatjun.ecommerce.mbg.model.Brand;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class PmsAdminBrandEvent {

    public enum Type {
        CREATE,
        UPDATE,
        DELETE
    }

    private final Type eventType;
    private final Brand brand;
    private final Integer brandId;
    private final ZonedDateTime eventCreatedAt;

    // Jackson needs it, (the library used for JSON serialization/deserialization)
    public PmsAdminBrandEvent() {
        this.eventType = null;
        this.brand = null;
        this.brandId = null;
        this.eventCreatedAt = null;
    }

    public PmsAdminBrandEvent(Type eventType, Brand brand, Integer brandId) {
        this.eventType = eventType;
        this.brand = brand;
        this.brandId = brandId;
        this.eventCreatedAt = now();
    }
}
