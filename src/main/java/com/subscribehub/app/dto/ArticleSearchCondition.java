package com.subscribehub.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleSearchCondition {
    private String email;
    private List<String> keywordList;
    private Long siteId;
}