package com.subscribehub.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Site extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "site_id")
    private Long id;

    private String siteUrl;

    private String siteNickname;

    @OneToMany(mappedBy = "site")
    private List<Article> articleList = new ArrayList<>();

    @OneToMany(mappedBy = "site")
    private List<UserSite> userSiteList;
}
