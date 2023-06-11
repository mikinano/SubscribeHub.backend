package com.subscribehub.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class Article extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id")
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_site_id")
    private UserSite userSite;

    private Long articleNum;
    private String url;
    private String title;

    private String writer;

    private LocalDateTime written_date;

    private Long viewCount;
    private Long recommendCount;
    private Long commentCount;

    public Article(User user, Site site, UserSite userSite, Long articleNum, String url, String title, String writer, LocalDateTime written_date, Long viewCount, Long recommendCount, Long commentCount) {
        this.user = user;
        this.site = site;
        this.userSite = userSite;
        this.articleNum = articleNum;
        this.url = url;
        this.title = title;
        this.writer = writer;
        this.written_date = written_date;
        this.viewCount = viewCount;
        this.recommendCount = recommendCount;
        this.commentCount = commentCount;
    }
}
