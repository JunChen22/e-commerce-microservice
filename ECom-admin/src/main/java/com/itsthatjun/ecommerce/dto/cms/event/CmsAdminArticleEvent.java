package com.itsthatjun.ecommerce.dto.cms.event;

import com.itsthatjun.ecommerce.dto.cms.AdminArticleInfo;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class CmsAdminArticleEvent {

    public enum Type {
        CREATE,
        UPDATE,
        DELETE
    }

    private final Type eventType;
    private final AdminArticleInfo articleInfo;
    private final String operator;
    private final ZonedDateTime eventCreatedAt;

    public CmsAdminArticleEvent(Type eventType, AdminArticleInfo articleInfo, String operator) {
        this.eventType = eventType;
        this.articleInfo = articleInfo;
        this.operator = operator;
        this.eventCreatedAt = now();
    }
}
