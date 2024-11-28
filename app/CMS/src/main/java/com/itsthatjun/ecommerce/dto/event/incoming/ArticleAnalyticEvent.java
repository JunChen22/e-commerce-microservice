package com.itsthatjun.ecommerce.dto.event.incoming;

import com.itsthatjun.ecommerce.dto.model.ArticleAnalyticDTO;
import lombok.Getter;

import java.time.ZonedDateTime;

import static java.time.ZonedDateTime.now;

@Getter
public class ArticleAnalyticEvent {

    private final int articleId;
    private final ArticleAnalyticDTO articleAnalytic;
    private final ZonedDateTime eventCreatedAt;

    public ArticleAnalyticEvent(int articleId, ArticleAnalyticDTO articleAnalytic) {
        this.articleId = articleId;
        this.articleAnalytic = articleAnalytic;
        this.eventCreatedAt = now();
    }
}
