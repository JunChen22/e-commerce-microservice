package com.itsthatjun.ecommerce.dto.event.cms;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class ArticleUpdateEvent {

    public enum Type {
        CREATE,
        UPDATE,
        DELETE
    }

    private final Type eventType;
    private final ArticleInfo articleInfo;
    private final ZonedDateTime eventCreatedAt;

    public ArticleUpdateEvent(Type eventType, ArticleInfo articleInfo) {
        this.eventType = eventType;
        this.articleInfo = articleInfo;
        this.eventCreatedAt = now();
    }
}
