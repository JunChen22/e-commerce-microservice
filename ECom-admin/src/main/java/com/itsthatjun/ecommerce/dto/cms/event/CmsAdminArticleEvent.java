package com.itsthatjun.ecommerce.dto.cms.event;

import com.itsthatjun.ecommerce.dto.cms.ArticleInfo;
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
    private final Integer articleID;
    private final ArticleInfo articleInfo;
    private final ZonedDateTime eventCreatedAt;

    public CmsAdminArticleEvent() {
        this.eventType = null;
        this.articleID = null;
        this.articleInfo = null;
        this.eventCreatedAt = null;
    }

    public CmsAdminArticleEvent(Type eventType, ArticleInfo articleInfo, Integer articleID) {
        this.eventType = eventType;
        this.articleID = articleID;
        this.articleInfo = articleInfo;
        this.eventCreatedAt = now();
    }
}
