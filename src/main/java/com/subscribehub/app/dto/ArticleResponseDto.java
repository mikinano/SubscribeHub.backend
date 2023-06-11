package com.subscribehub.app.dto;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class ArticleResponseDto {
    private Page<ArticleDto> pagingResult;

    private List<ArticleDto> updatedList;

    public ArticleResponseDto(Page<ArticleDto> pagingResult, List<ArticleDto> updatedList) {
        this.pagingResult = pagingResult;
        this.updatedList = updatedList;
    }
}
