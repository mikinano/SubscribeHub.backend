package com.subscribehub.app.repository;

import com.subscribehub.app.domain.Article;
import com.subscribehub.app.domain.User;
import com.subscribehub.app.domain.UserSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    List<Article> findByUserSiteAndArticleNumIn(UserSite userSite, List<Long> articleNumList);
}
