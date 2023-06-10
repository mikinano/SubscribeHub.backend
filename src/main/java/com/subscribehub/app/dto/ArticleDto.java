package com.subscribehub.app.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class ArticleDto {
    private String completeUrl;
    private String title;
    private LocalDateTime written_date;

    @QueryProjection
    public ArticleDto(String completeUrl, String title, LocalDateTime written_date) {
        this.completeUrl = completeUrl;
        this.title = title;
        this.written_date = written_date;
    }
}
