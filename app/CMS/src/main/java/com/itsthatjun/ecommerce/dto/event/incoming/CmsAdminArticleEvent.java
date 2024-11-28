package com.itsthatjun.ecommerce.dto.event.incoming;

import com.itsthatjun.ecommerce.dto.admin.AdminArticleInfo;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class CmsAdminArticleEvent {

    public enum Type {
        CREATE,
        UPDATE,
        UPDATE_STATUS,
        RESTORE_ARTICLE,
        DELETE,
        DELETE_PERMANENTLY
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
