package com.subscribehub.app.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleDto {
    private Long articleId;
    private Long articleNum;
    private String url;

    private String nickname;
    private String title;

    private String writer;

    private LocalDateTime written_date;

    private Long viewCount;
    private Long recommendCount;
    private Long commentCount;

    @QueryProjection
    public ArticleDto(Long articleId, Long articleNum, String url, String nickname, String title, String writer, LocalDateTime written_date, Long viewCount, Long recommendCount, Long commentCount) {
        this.articleId = articleId;
        this.articleNum = articleNum;
        this.url = url;
        this.nickname = nickname;
        this.title = title;
        this.writer = writer;
        this.written_date = written_date;
        this.viewCount = viewCount;
        this.recommendCount = recommendCount;
        this.commentCount = commentCount;
    }
}
